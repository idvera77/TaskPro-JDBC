package com.taskpro;

import com.taskpro.dao.TareaDAO;
import com.taskpro.model.Tarea;
import com.taskpro.model.enums.EstadoTarea;
import com.taskpro.model.enums.Prioridad;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        TareaDAO dao = new TareaDAO();
        System.out.println("--- INICIANDO ACTUALIZACIÓN ---");

        // 1. Elegimos un ID que sepamos que existe en la BD
        long idReal = 7; // <-- CAMBIA ESTE NÚMERO POR UNO TUYO QUE EXISTA

        // 2. Creamos un objeto Tarea con ese ID, pero con datos NUEVOS
        Tarea tareaModificada = new Tarea(idReal, 1, "Título Modificado", "Le" +
                " he cambiado la descripción", Prioridad.ALTA, EstadoTarea.DONE,
                LocalDate.now());

        // 3. Se lo mandamos al DAO y escuchamos su respuesta
        if (dao.actualizar(tareaModificada)) {
            System.out.println("✅ ÉXITO: La tarea " + idReal + " ha sido actualizada a COMPLETADA.");
        } else {
            System.out.println("⚠️ AVISO: No se encontró la tarea con ID " + idReal + " o no se pudo actualizar.");
        }

    }
}