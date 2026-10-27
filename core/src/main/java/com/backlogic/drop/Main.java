package com.backlogic.drop;


import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main implements ApplicationListener {
    Texture backgroundTexture;
    Texture bucketTexture;
    Sprite padelOne;
    Texture dropTexture;
    Sound dropSound;
    Music music;
    Array<Sprite> dropSprites;
    SpriteBatch spriteBatch;
    FitViewport viewport;
    public BitmapFont font;
    float dropTimer;
    Vector2 touchPos;
    Rectangle bucketRectangle;
    Rectangle dropRectangle;
    float DropSpeedY;
    float DropSpeedX;
    float BucketSpeedX;
    float RANDOMX;

    public Main() {
    }

    @Override
    public void create() {
        // Prepare your application here.
        bucketTexture = new Texture("assets_game1/padel.png");
        backgroundTexture = new Texture("assets_game1/background.jpg");
        dropTexture = new Texture("assets_game1/drop.png");
        padelOne = new Sprite(bucketTexture);
        padelOne.setSize(1, 1);
        dropSound = Gdx.audio.newSound(Gdx.files.internal("assets_game1/drop.mp3"));
        music = Gdx.audio.newMusic(Gdx.files.internal("assets_game1/music.mp3"));
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(8, 5);
        dropSprites = new Array<>();
        touchPos = new Vector2();
        bucketRectangle = new Rectangle();
        dropRectangle = new Rectangle();


        music.setLooping(true);
        music.setVolume(.5f);
        //music.play();
        createDroplet();




    }


    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if(width <= 0 || height <= 0) return;

        // Resize your application here. The parameters represent the new window size.
    }
    private void logic() {
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        float bucketWidth = padelOne.getWidth();
        float bucketHeight = padelOne.getHeight();


        padelOne.setX(MathUtils.clamp(padelOne.getX(), 0, worldWidth - bucketWidth));


        float delta = Gdx.graphics.getDeltaTime();
        bucketRectangle.set(padelOne.getX(), padelOne.getY(), bucketWidth, bucketHeight);
        BucketSpeedX = BucketSpeedX * 0.9f;
        padelOne.translateX(BucketSpeedX * delta);
        // Loop through the sprites backwards to prevent out of bounds errors
        for (int i = dropSprites.size - 1; i >= 0; i--) {
            Sprite dropSprite = dropSprites.get(i); // Get the sprite from the list
            float dropWidth = dropSprite.getWidth();
            float dropHeight = dropSprite.getHeight();
            DropSpeedY = DropSpeedY - (7f * delta);
            DropSpeedX = DropSpeedX * 0.95f;
            dropSprite.translateY((DropSpeedY) * delta);
            // Apply the drop position and size to the dropRectangle
            dropRectangle.set(dropSprite.getX(), dropSprite.getY(), dropWidth, dropHeight);
            dropSprite.translateX(DropSpeedX * delta);
            if (dropSprite.getY() < -dropHeight) {
                dropSprites.removeIndex(i);
                createDroplet();
                DropSpeedY=0f;
                DropSpeedX=0f;
            }
            if (dropSprite.getX() <= 0 || dropSprite.getX() >= worldWidth-1) {
                DropSpeedX = -DropSpeedX;
                dropSprite.translateX(DropSpeedX * delta);
                dropSound.play();
            }
            if (bucketRectangle.overlaps(dropRectangle)) { // Check if the bucket overlaps the drop
                //dropSprites.removeIndex(i); // Remove the drop
                DropSpeedY = 5;
                RANDOMX = MathUtils.random(0, 3);
                DropSpeedX = (float) (DropSpeedX + (BucketSpeedX * 2 + RANDOMX));
                dropSound.play();

            }
        }

        dropTimer += delta;
        if (dropTimer > 1f) {
            dropTimer = 0;

        }
    }
    private void draw()
    {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();


        spriteBatch.draw(backgroundTexture, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        padelOne.draw(spriteBatch);
        for (Sprite dropSprite : dropSprites) {
            dropSprite.draw(spriteBatch);
        }
        logic();
        spriteBatch.end();




    }


    private void input() {
        float speed = 4f;
        float delta = Gdx.graphics.getDeltaTime();


        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            BucketSpeedX = 7f;
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            BucketSpeedX = -7f;
        }


        //if (Gdx.input.isTouched()) {
          //  touchPos.set(Gdx.input.getX(), Gdx.input.getY()); // Get where the touch happened on screen
            //viewport.unproject(touchPos); // Convert the units to the world units of the viewport
            //padelOne.setCenterX(touchPos.x); // Change the horizontally centered position of the bucket
        //}
    }


    private void createDroplet() {
        float dropWidth = 1;
        float dropHeight = 1;
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();




        // create the drop sprite
        Sprite dropSprite = new Sprite(dropTexture);
        dropSprite.setSize(dropWidth, dropHeight);
        dropSprite.setX(MathUtils.random(0f, worldWidth - dropWidth));
        dropSprite.setY(worldHeight);
        dropSprites.add(dropSprite);
    }


    @Override
    public void render() {
        // Draw your application here.
        draw();
        input();
    }


    @Override
    public void pause() {
        // Invoked when your application is paused.
    }


    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }


    @Override
    public void dispose() {
        // Destroy application's resources here.
    }
}








