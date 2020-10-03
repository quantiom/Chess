package me.quantiom.chess.render;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImageResize.*;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;

public class TextureUtil {
    private static void premultiplyAlpha(Texture texture) {
        int stride = texture.getWidth() * 4;
        for (int y = 0; y < texture.getHeight(); y++) {
            for (int x = 0; x < texture.getWidth(); x++) {
                int i = y * stride + x * 4;
                ByteBuffer image = texture.getImage();

                float alpha = (image.get(i + 3) & 0xFF) / 255.0f;
                image.put(i + 0, (byte) Math.round(((image.get(i + 0) & 0xFF) * alpha)));
                image.put(i + 1, (byte) Math.round(((image.get(i + 1) & 0xFF) * alpha)));
                image.put(i + 2, (byte) Math.round(((image.get(i + 2) & 0xFF) * alpha)));
            }
        }
    }

    public static Texture createTexture(String resource) {
        Texture texture = new Texture(resource);

        glBindTexture(GL_TEXTURE_2D, texture.getTexId());
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        int format;
        if (texture.getComp() == 3) {
            if ((texture.getWidth() & 3) != 0) {
                glPixelStorei(GL_UNPACK_ALIGNMENT, 2 - (texture.getWidth() & 1));
            }
            format = GL_RGB;
        } else {
            premultiplyAlpha(texture);

            glEnable(GL_BLEND);
            glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

            format = GL_RGBA;
        }

        glTexImage2D(GL_TEXTURE_2D, 0, format, texture.getWidth(), texture.getHeight(), 0, format, GL_UNSIGNED_BYTE, texture.getImage());

        ByteBuffer input_pixels = texture.getImage();
        int input_w = texture.getWidth();
        int input_h = texture.getHeight();
        int mipmapLevel = 0;
        while (1 < input_w || 1 < input_h) {
            int output_w = Math.max(1, input_w >> 1);
            int output_h = Math.max(1, input_h >> 1);

            ByteBuffer output_pixels = memAlloc(output_w * output_h * texture.getComp());
            stbir_resize_uint8_generic(
                    input_pixels, input_w, input_h, input_w * texture.getComp(),
                    output_pixels, output_w, output_h, output_w * texture.getComp(),
                    texture.getComp(), texture.getComp() == 4 ? 3 : STBIR_ALPHA_CHANNEL_NONE, STBIR_FLAG_ALPHA_PREMULTIPLIED,
                    STBIR_EDGE_CLAMP,
                    STBIR_FILTER_MITCHELL,
                    STBIR_COLORSPACE_SRGB
            );

            if (mipmapLevel == 0) {
                stbi_image_free(texture.getImage());
            } else {
                memFree(input_pixels);
            }

            glTexImage2D(GL_TEXTURE_2D, ++mipmapLevel, format, output_w, output_h, 0, format, GL_UNSIGNED_BYTE, output_pixels);

            input_pixels = output_pixels;
            input_w = output_w;
            input_h = output_h;
        }
        if (mipmapLevel == 0) {
            stbi_image_free(texture.getImage());
        } else {
            memFree(input_pixels);
        }

        return texture;
    }
}
