package me.quantiom.chess.render;

import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static me.quantiom.chess.util.IOUtil.ioResourceToByteBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Texture {
    private ByteBuffer image;
    private int width;
    private int height;
    private int comp;
    private int texId;

    public Texture(String resource) {
        ByteBuffer imageBuffer;

        try {
            imageBuffer = ioResourceToByteBuffer(resource, 8 * 1024);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (MemoryStack stack = stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            if (!stbi_info_from_memory(imageBuffer, w, h, comp)) {
                throw new RuntimeException("Failed to read image information: " + stbi_failure_reason());
            }

            image = stbi_load_from_memory(imageBuffer, w, h, comp, 0);
            if (image == null) {
                throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
            }

            this.width = w.get(0);
            this.height = h.get(0);
            this.comp = comp.get(0);
        }

        this.texId = glGenTextures();
    }

    public void draw(int x, int y) {
        glColor4f(1.f, 1.f, 1.f, 1.f);
        glBindTexture(GL_TEXTURE_2D, this.getTexId());
        glEnable(GL_TEXTURE_2D);
        glBegin(GL_QUADS);
        {
            glTexCoord2f(0.0f, 0.0f);
            glVertex2f(x, y);

            glTexCoord2f(1.0f, 0.0f);
            glVertex2f(x + this.getWidth(), y);

            glTexCoord2f(1.0f, 1.0f);
            glVertex2f(x + this.getWidth(), y + this.getHeight());

            glTexCoord2f(0.0f, 1.0f);
            glVertex2f(x, y + this.getHeight());
        }
        glEnd();
        glDisable(GL_TEXTURE_2D);
    }

    public int getTexId() {
        return texId;
    }

    public ByteBuffer getImage() {
        return image;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getComp() {
        return comp;
    }
}
