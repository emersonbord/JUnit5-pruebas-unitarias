package org.eborda.junitapp.ejemplos.models;

import org.eborda.junitapp.ejemplos.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CuentaTest {

    //Para indicar a la plataforma al test engine de que es este método debe ser ejectutar como prueba unitaria, con la siguiente anotación
    @Test
    @DisplayName("Probando nombre de la cuenta!")
    void testNombreCuenta() {
        //Preparamos nuestra prueba unitaria
        Cuenta cuenta = new Cuenta("Emerson", new BigDecimal("1000.123")); //Instanciamos la cuenta
        //pasamos datos, input que son datos de pruebas
        //cuenta.setPersona("Emerson");
        //Cómo afirmar que esto es cierto? siempre en pruebas unitarias vamos a tener un valor esperado o espectativa
        String esperado = "Emerson";
        //versus la realidad
        String real = cuenta.getPersona();
        assertNotNull(real, () -> "La cuenta no puede ser nula");

        //Sino se verifica nada, el test es correcto.
        assertEquals(esperado, real, () -> "El nombre de la cuenta no es el que se esperaba" + esperado
                + " sin embargo fue " + real); //assertions es afirmar un valor que sea el correcto el que estamos esperando
        assertTrue(real.equals("Emerson"), () -> "El nombre cuenta esperada debe ser igual a la real"); //dentro del assertTrue, tenemos una expresión booleana
    }

    @Test
    @DisplayName("Verificar el saldo de la cuenta conrriente, que no sea null, mayor que ser, valor esperado!")
    void testSaldoCuenta() {
        Cuenta cuenta = new Cuenta("Emerson", new BigDecimal("1000.123"));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1000.123, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);

    }

    @Test
    @DisplayName("Testeando referencias que sean iguales con el método equals.")
    void testReferenciaCuenta() {
        Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("8900.9997"));
        Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("8900.9997"));
        //assertNotEquals(cuenta2, cuenta); // cuenta es nuestro valor real o actual, y cuenta2 valor esperado, comparamos que estos dos sean distintos
        assertEquals(cuenta2, cuenta); //

    }

    @Test
    void testDebitoCuenta() {
        Cuenta cuenta = new Cuenta("Emerson", new BigDecimal("1000.123"));
        cuenta.debito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.123", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testCreditoCuenta() {
        Cuenta cuenta = new Cuenta("Emerson", new BigDecimal("1000.123"));
        cuenta.credito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1100, cuenta.getSaldo().intValue());
        assertEquals("1100.123", cuenta.getSaldo().toPlainString()); //nuestro valor esperado
    }

    @Test
    void testDineroInsuficienteException() {
        Cuenta cuenta = new Cuenta("Emerson", new BigDecimal("1000.123"));
        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
            //invocamos al método que va a lanzar la excepción
            cuenta.debito(new BigDecimal(1600));//se captura
        });
        String actual = exception.getMessage();
        String esperado = "Dinero Insuficiente";
        assertEquals(esperado, actual);
    }

    @Test
    void testTransferirDineroCuentas() {
        Cuenta cuenta1 = new Cuenta("John Doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Emerson", new BigDecimal("1500.8989"));
        Banco banco = new Banco();
        banco.setNombre("Banco del Estado");
        banco.transferir(cuenta2, cuenta1, new BigDecimal(500));
        assertEquals("1000.8989", cuenta2.getSaldo().toPlainString());
        assertEquals("3000", cuenta1.getSaldo().toPlainString());
    }

    @Test
    @Disabled //No ejecuta el método testRelacionBandoCuentas. Es para saltar el método de prueba
    @DisplayName("Probando relaciones entre las cuentas y el bando con assertAll")
    void testRelacionBancoCuentas() {
        fail(); //fuerza el error, es un mmpetodo estático de la clase assertions, aseguramos que fallará el método
        Cuenta cuenta1 = new Cuenta("John Doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Emerson", new BigDecimal("1500.8989"));

        Banco banco = new Banco();
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);

        banco.setNombre("Banco del Estado");
        banco.transferir(cuenta2, cuenta1, new BigDecimal(500));
        assertAll(() ->
                {
                    assertEquals("1000.8989", cuenta2.getSaldo().toPlainString(),
                            () -> "El valor del saldo de la cuenta2 no es el esperado");
                },
                () -> {
                    assertEquals("3000", cuenta1.getSaldo().toPlainString(),
                            () -> "El banco no tiene las cuentas esperadas");
                },
                () -> {
                    assertEquals(2, banco.getCuentas().size());
                },
                () -> {
                    assertEquals("Banco del Estado", cuenta1.getBanco().getNombre());
                },
                () -> {
                    assertEquals("Emerson", banco.getCuentas().stream()
                            .filter(c -> c.getPersona().equals("Emerson"))
                            .findFirst()
                            .get().getPersona());
                },
                () -> {
                    assertTrue(banco.getCuentas().stream()
                            .anyMatch(c -> c.getPersona().equals("Emerson")));
                });

        //probar la relación en ambos sentidos, 2 es la cantidad de cuentas
        //assertEquals(2, banco.getCuentas().size());

    }
}