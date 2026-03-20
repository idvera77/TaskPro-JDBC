package com.taskpro.exception;

/**
 * Excepción para errores durante el proceso de inicio de sesión.
 */
public class AuthenticationException extends RuntimeException {

    /**
     * Constructor con un mensaje descriptivo del error.
     * @param mensaje Detalle de lo que falló.
     */
    public AuthenticationException(String mensaje) {
        super(mensaje);
    }
}