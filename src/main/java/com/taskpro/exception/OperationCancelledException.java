package com.taskpro.exception;

/**
 * Excepción de flujo para cancelar operaciones en curso sin cerrar el programa.
 */
public class OperationCancelledException extends RuntimeException {

    public OperationCancelledException() {
        super("Operación cancelada por el usuario.");
    }
}