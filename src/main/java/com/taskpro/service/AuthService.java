package com.taskpro.service;

import com.taskpro.dao.UsuarioDAO;
import com.taskpro.exception.AuthenticationException;
import com.taskpro.model.Usuario;

public class AuthService {
    private final UsuarioDAO usuarioDAO;

    /**
     * Inicializa el servicio de autenticación y su acceso a datos.
     */
    public AuthService() {
        this.usuarioDAO = new UsuarioDAO();
    }

    /**
     * Realiza la validación de acceso al sistema comparando credenciales.
     *
     * @param email El correo electrónico introducido por el usuario.
     * @param password La contraseña introducida en el formulario de acceso.
     * @return El objeto Usuario si las credenciales coinciden, o null si son incorrectas.
     */
    public Usuario login(String email, String password) {
        Usuario usuario = usuarioDAO.buscarPorEmail(email);

        if (usuario == null || !usuario.getPassword().equals(password)) {
            throw new AuthenticationException("Email o contraseña incorrectos. Inténtalo de nuevo.");
        }

        return usuario;
    }
}