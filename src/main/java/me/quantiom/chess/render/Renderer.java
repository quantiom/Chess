package me.quantiom.chess.render;

import me.quantiom.chess.util.Color;

import static java.lang.Math.cos;
import static java.lang.StrictMath.sin;
import static me.quantiom.chess.util.Constants.SQUARE_SIZE;
import static org.lwjgl.opengl.GL11.*;

public class Renderer {
    private static final float DEG2RAD = 3.14159f / 180f;

    public static void rectangle(int x, int y, int width, int height, int r, int g, int b, int a) {
        glBegin(GL_QUADS);
        {
            glColor4f(r / 255.f, g / 255.f, b / 255.f, a / 255.f);

            glVertex2f(x, y);
            glVertex2f(x + width, y);
            glVertex2f(x + width, y + height);
            glVertex2f(x, y + height);
        }
        glEnd();
    }

    public static void rectangle(int x, int y, int width, int height, Color color) {
        rectangle(x, y, width, height, color.r, color.g, color.b, color.a);
    }

    public static void circle(int x, int y, int radius, Color color) {
        glMatrixMode(GL_MODELVIEW);
        glPushMatrix();
        glLoadIdentity();

        glColor4f(color.r / 255.f, color.g / 255.f, color.b / 255.f, color.a / 255.f);
        glTranslatef(((float)x / (SQUARE_SIZE * 4)) - 1, (((float)y / (SQUARE_SIZE * 4)) - 1) * -1, 0.f);

        float fRadius = (float)radius / SQUARE_SIZE;
        final int circle_points = 100;
        final float angle = 2.0f * 3.1416f / circle_points;

        glBegin(GL_POLYGON);

        double angle1 = 0.0;

        glVertex2d(fRadius * cos(0.0), fRadius * sin(0.0));

        for (int i = 0; i < circle_points; i++) {
            glVertex2d(fRadius * cos(angle1), fRadius * sin(angle1));
            angle1 += angle;
        }

        glEnd();
        glPopMatrix();
    }

    public static void circle(int x, int y, int radius, int r, int g, int b, int a) {
        circle(x, y, radius, new Color(r, g, b, a));
    }
}
