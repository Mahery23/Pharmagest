package com.example.pharmagest.login;

// Gestion de la session utilisateur
public class UserSession {
    // Statut de l'utilisateur (Admin/Vendeur)
    private static String currentUserStatus;
    // ID de l'utilisateur connecté
    private static int currentUserId;

    /**
     * Enregistre le statut de l'utilisateur dans la session
     * @param status Le statut de l'utilisateur (Admin ou Vendeur)
     */
    public static void setUserStatus(String status) {
        currentUserStatus = status;
    }

    /**
     * Récupère le statut de l'utilisateur connecté
     * @return Le statut de l'utilisateur
     */
    public static String getUserStatus() {
        return currentUserStatus;
    }

    /**
     * Enregistre l'ID de l'utilisateur dans la session
     * @param id L'ID de l'utilisateur
     */
    public static void setUserId(int id) {
        currentUserId = id;
    }

    /**
     * Récupère l'ID de l'utilisateur connecté
     * @return L'ID de l'utilisateur
     */
    public static int getUserId() {
        return currentUserId;
    }

    /**
     * Efface la session lors de la déconnexion
     * Appelé lors de la déconnexion ou du changement d'utilisateur
     */
    public static void clearSession() {
        currentUserStatus = null;
        currentUserId = 0;
    }
} 