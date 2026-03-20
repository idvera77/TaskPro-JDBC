package com.taskpro.exception;

/**
 * Excepción personalizada para indicar que un recurso no ha sido hallado en la base de datos.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructor con un mensaje descriptivo del error.
     * @param mensaje Detalle de lo que falló.
     */
    public ResourceNotFoundException(String mensaje) {
        super(mensaje);
    }
}