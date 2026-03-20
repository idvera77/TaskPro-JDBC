package com.taskpro.exception;

/**
 * Excepción personalizada para indicar cuando un usuario intenta hacer algo que su rol no le
 * permite.
 */
public class AccessDeniedException extends RuntimeException {

    /**
     * Constructor con un mensaje descriptivo del error.
     * @param mensaje Detalle de lo que falló.
     */
    public AccessDeniedException(String mensaje) {
        super(mensaje);
    }
}