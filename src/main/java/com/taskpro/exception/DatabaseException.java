package com.taskpro.exception;

/**
 * Excepción personalizada para errores técnicos inesperados en la capa de persistencia.
 * Se utiliza cuando la base de datos falla por causas ajenas a la lógica de negocio
 */
public class DatabaseException extends RuntimeException {

    /**
     * Constructor con un mensaje descriptivo del error.
     * @param mensaje Detalle de lo que falló.
     */
    public DatabaseException(String mensaje) {
        super(mensaje);
    }

    /**
     * Constructor con mensaje y causa original.
     * Útil para capturar una SQLException y relanzarla como DatabaseException.
     * @param mensaje Detalle del error.
     * @param causa La excepción original (ej. SQLException).
     */
    public DatabaseException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}