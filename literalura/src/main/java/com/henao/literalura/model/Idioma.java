package com.henao.literalura.model;

public enum Idioma {
    ESPAÑOL("es", "Español"),
    INGLES("en", "Ingles");

    private String idiomaGutendex;
    private String idiomaNormal;

    Idioma(String idiomaGutendex, String idiomaNormal){
        this.idiomaGutendex = idiomaGutendex;
        this.idiomaNormal = idiomaNormal;
    }

    public static Idioma fromString(String text) {
        for (Idioma idioma : Idioma.values()) {
            if (idioma.idiomaGutendex.equalsIgnoreCase(text)) {
                return idioma;
            }
        }
        throw new IllegalArgumentException("Ningun idioma encontrado");
    }

    public static Idioma fromNormal(String text) {
        try {
            for (Idioma idioma : Idioma.values()) {
                if (idioma.idiomaNormal.equalsIgnoreCase(text)) {
                    return idioma;
                }
            }
            throw new IllegalArgumentException("Ningún idioma encontrado");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }
}
