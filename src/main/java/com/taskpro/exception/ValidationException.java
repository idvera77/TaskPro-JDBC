package com.taskpro.exception;

/**
 * Excepción personalizada para indicar cuando los datos están mal.
 */
public class ValidationException extends RuntimeException {

    /**
     * Constructor con un mensaje descriptivo del error.
     * @param mensaje Detalle de lo que falló.
     */
    public ValidationException(String mensaje) {
        super(mensaje);
    }
}