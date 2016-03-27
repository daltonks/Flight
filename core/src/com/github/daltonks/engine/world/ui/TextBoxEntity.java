//3D text box
//automatically sorts the draw list of letters for faster rendering
//supports scaled, rotated text
//uses 3D models located in the .plylite (supports a through z and some punctuation)

package com.github.daltonks.engine.world.ui;

import com.badlogic.gdx.math.Matrix4;
import com.github.daltonks.engine.states.EngineState;
import com.github.daltonks.engine.util.Color;
import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.util.Vec3d;
import com.github.daltonks.engine.world.camera.Camera;
import com.github.daltonks.engine.world.entityComponent.components.DrawableComponent;
import com.github.daltonks.engine.world.entityComponent.components.transformComponents.TransformComponent;
import com.github.daltonks.engine.world.entityComponent.entities.base.Entity;
import com.github.daltonks.engine.world.models.Model;
import com.github.daltonks.engine.world.models.Models;

import java.util.ArrayList;
import java.util.Collections;

public class TextBoxEntity extends UIEntity {
    public TextBoxEntity(EngineState engineState, double x, double y, double z, String text, float width, float textScale, Color textColor) {
        super(engineState, null, x, y, z);
        this.setUIBoundsComponent(new UIBoundsComponent(this, 0, 0, 1, 1));
        this.setDrawableComponent(new TextBoxDrawableComponent(this, text, width, textScale, textColor));
    }

    public TextBoxDrawableComponent getTextBoxDrawableComponent() {
        return (TextBoxDrawableComponent) this.getDrawableComponent();
    }

    public void onSurfaceChanged(EngineState engineState, int screenWidth, int screenHeight) {
        super.onSurfaceChanged(engineState, screenWidth, screenHeight);
        getTextBoxDrawableComponent().updateLetterLocations();
    }

    public class TextBoxDrawableComponent extends DrawableComponent {
        static final float CHAR_HEIGHT = 2;
        static final float SPACE_WIDTH = 1;
        static final float SPACER = .12f;

        private float textScale, wordWrapWidth, width, height;
        private Color textColor;
        private ArrayList<Letter> letters = new ArrayList<Letter>();

        public TextBoxDrawableComponent(Entity entity, String text, float wordWrapWidth, float textScale, Color textColor) {
            super(entity);
            this.wordWrapWidth = wordWrapWidth;
            this.textScale = textScale;
            this.textColor = textColor;
            setText(text);
        }

        @Override
        public void update(EngineState engineState, double delta) {

        }

        @Override
        public void draw(Camera camera) {
            for(int i = 0; i < letters.size(); i++) {
                Letter letter = letters.get(i);
                if(camera.getFrustumCuller().isSphereInFrustum(letter.loc, letter.model.getModelInfo().radius * textScale)) {
                    letter.model.draw(
                            letter.loc,
                            getEntity().getTransformComponent().getRotationMatrix(),
                            textScale, textScale, textScale,
                            textColor,
                            camera
                    );
                }
            }
        }

        public void setText(String text) {
            text = text.toLowerCase();
            float topLeftX = 0, topLeftY = 0;
            letters.clear();
            String[] words = text.split(" ");
            for(String word : words) {
                //word wrap
                int wordWidth = 0;
                for(int i = 0; i < word.length() - 1; i++) {
                    wordWidth += getCharWidth(word.charAt(i)) + getSpacerWidth();
                }
                wordWidth += getCharWidth(word.charAt(word.length() - 1));
                if(topLeftX + wordWidth > wordWrapWidth) {
                    topLeftX = 0;
                    topLeftY -= getCharHeight() + getSpacerWidth();
                }
                for(int i = 0; i < word.length(); i++) {
                    char c = word.charAt(i);
                    float charWidth = getCharWidth(c);
                    Letter letter = new Letter(c, topLeftX + charWidth / 2, topLeftY + getCharHeight() / 2);
                    letters.add(letter);
                    topLeftX += charWidth + getSpacerWidth();
                }
                if(topLeftX > width) {
                    width = topLeftX;
                }
                topLeftX += SPACE_WIDTH * textScale;
            }

            height = Math.abs(topLeftY) + getCharHeight();

            for(int i = 0; i < letters.size(); i++) {
                Letter letter = letters.get(i);
                letter.offsetX -= width / 2;
                letter.offsetY -= height / 2;
            }

            //put same letters together in list for faster rendering
            Collections.sort(letters);
            updateLetterLocations();

            TextBoxEntity.this.getUIBoundsComponent().setWidth(width);
            TextBoxEntity.this.getUIBoundsComponent().setHeight(height);
        }

        public void updateLetterLocations() {
            TransformComponent transform = getEntity().getTransformComponent();
            for(int i = 0; i < letters.size(); i++) {
                Letter letter = letters.get(i);
                Vec3d offset = Pools.getVec3d();
                offset.set(letter.offsetX, letter.offsetY, 0);
                Matrix4 rotMatrix = transform.getRotationMatrix();
                offset.multMatrix(rotMatrix);
                Vec3d loc = transform.getLocation();
                letter.loc.set(loc.x() + offset.x(), loc.y() + offset.y(), loc.z() + offset.z());
                Pools.recycle(offset);
            }
        }

        private float getSpacerWidth() {
            return SPACER * textScale;
        }

        private float getCharWidth(char c) {
            Model model = getModel(c);
            float lowX = model.getModelInfo().minX;
            float highX = model.getModelInfo().maxX;
            return (highX - lowX) * textScale;
        }

        private float getCharHeight() {
            return CHAR_HEIGHT * textScale;
        }

        public float getWidth() {
            return width;
        }

        public float getHeight() {
            return height;
        }

        public void setTextColor(Color color) {
            this.textColor = color;
        }

        public Color getTextColor() {
            return textColor;
        }
    }

    private static Model getModel(char c) {
        if(c >= 'a' && c <= 'z') {
            return Models.get(c + "");
        } else {
            switch(c) {
                case '.':
                    return Models.get("period");
                case '!':
                    return Models.get("exclamation");
                case ',':
                    return Models.get("comma");
                case ':':
                    return Models.get("colon");
                default:
                    return null;
            }
        }
    }

    private static class Letter implements Comparable<Letter> {
        char c;
        Model model;
        float offsetX, offsetY;
        Vec3d loc = new Vec3d();

        Letter(char c, float offsetX, float offsetY) {
            this.c = c;
            this.model = getModel(c);
            this.offsetX = offsetX;
            this.offsetY = offsetY;
        }

        @Override
        public int compareTo(Letter another) {
            if(c < another.c) {
                return -1;
            } else if(c > another.c) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}