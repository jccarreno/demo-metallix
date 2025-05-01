package com.atix.demo.domain.exceptions;

public class DuplicateCreditNoteException extends RuntimeException {
    public DuplicateCreditNoteException(String noteNumber) {
        super("Nota de crédito con número '" + noteNumber + "' ya existe.");
    }
}

