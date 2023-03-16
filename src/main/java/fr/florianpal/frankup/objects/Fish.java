package fr.florianpal.frankup.objects;

public class Fish {
    private final String id;
    private final String name;
    private final String texture;

    public Fish(String id, String name, String texture) {
        this.id = id;
        this.name = name;
        this.texture = texture;
    }

    public String getTexture() {
        return texture;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
