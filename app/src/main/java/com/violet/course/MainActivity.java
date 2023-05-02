package com.violet.course;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    public Game game = new Game();
    public static GridView preGridView, rusGridView, hohGridView;
    public static Player russianPlayer, hoholPlayer;
    public static MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(params);
        mediaPlayer = MediaPlayer.create(this, R.raw.andrew);
        mediaPlayer.start();
    }

    class Game {
        Context context;
        TextView statusTextView;
        ConstraintLayout gameConstraintLayout;
        public Status status = Status.PREPARE;
        private final int[] badSounds = {R.raw.opa, R.raw.hvatit, R.raw.ugroza};
        private final int[] goodSounds = {R.raw.povezlo, R.raw.probit};
        private final int[] memes = {R.drawable.ded, R.drawable.don, R.drawable.rock};

        void start(Context context) {
            this.context = context;
            statusTextView = findViewById(R.id.gameStatusTextView);
            russianNext();
            gameConstraintLayout = findViewById(R.id.gameConstraintLayout);
            hohGridView = findViewById(R.id.hoholGridView);
            rusGridView = findViewById(R.id.russianGridView);
            rusGridView.post(() -> {
                for (int i = 0; i < 10; i++) {
                    ImageView imageView = new ImageView(context);
                    imageView.setImageResource(R.drawable.button_border);
                    gameConstraintLayout.addView(imageView);
                    float step = rusGridView.getHeight() / 10f;
                    int width, height;
                    if (russianPlayer.getShip(i).isVertical()) {
                        width = (int) step;
                        height = (int) step * russianPlayer.getShip(i).getLife();
                    } else {
                        width = (int) step * russianPlayer.getShip(i).getLife();
                        height = (int) step;
                    }
                    imageView.setLayoutParams(new ViewGroup.LayoutParams(width, height));
                    imageView.setX(rusGridView.getX() + step * russianPlayer.getShip(i).getStartX());
                    imageView.setY(rusGridView.getY() + step * russianPlayer.getShip(i).getStartY());
                }
            });
            hohGridView.post(new Runnable() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public void run() {
                    hohGridView.setOnTouchListener((view, event) -> {
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            if (status == Status.RUSSIAN) {
                                float step = (float) hohGridView.getHeight() / 10f;
                                float x = event.getRawX() - 90; //wtf ??
                                float y = event.getRawY();
                                int posX = (int) ((x - hohGridView.getX()) / hohGridView.getWidth() * 10);
                                int posY = (int) ((y - hohGridView.getY()) / hohGridView.getHeight() * 10);
                                ImageView imageView = new ImageView(context);
                                imageView.setImageResource(R.drawable.button_border);
                                int res = russianPlayer.ShotToEnemy(hoholPlayer, posX, posY);
                                if (res > -2) {
                                    status = Status.PREPARE;
                                    imageView.setColorFilter(Color.WHITE);
                                    if (res > -1) {
                                        imageView.setColorFilter(Color.RED);
                                        weDamage();
                                        if ((res / 10) == 0){
                                            int size = res % 10;
                                            TextView stat = findViewById(R.id.left4);
                                            switch (size) {
                                                case 3:
                                                    stat = findViewById(R.id.left3);
                                                    break;
                                                case 2:
                                                    stat = findViewById(R.id.left2);
                                                    break;
                                                case 1:
                                                    stat = findViewById(R.id.left1);
                                                    break;
                                            }
                                            int cur = Integer.parseInt(String.valueOf(stat.getText().charAt(1)));
                                            stat.setText("x" + (cur - 1));
                                        }
                                        if (hoholPlayer.isLose()) {
                                            weWon();
                                        }
                                    } else {
                                        hoholNext();
                                    }
                                    gameConstraintLayout.addView(imageView);
                                    imageView.setLayoutParams(new ViewGroup.LayoutParams((int) step - 1, (int) step - 1));
                                    imageView.setX(hohGridView.getX() + posX * step);
                                    imageView.setY(hohGridView.getY() + posY * step);
                                }
                            }
                        }
                        return true;
                    });
                }
            });
        }

        void weDamage() {
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
            Random rand = new Random();
            ImageView imageView = findViewById(R.id.memeImageView);
            mediaPlayer = MediaPlayer.create(context, goodSounds[rand.nextInt(goodSounds.length)]);
            mediaPlayer.start();
            imageView.setImageResource(memes[rand.nextInt(memes.length)]);
            new Handler().postDelayed(() -> {
                imageView.setImageDrawable(null);
                russianNext();
            }, 1000);
        }

        void weWon() {
            end("МЫ ВЫЙГРАЛИ !!!");
        }

        void weDamaged() {
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
            Random rand = new Random();
            mediaPlayer = MediaPlayer.create(context, badSounds[rand.nextInt(badSounds.length)]);
            mediaPlayer.start();
        }

        void weLose() {
            end("МЫ ПРОЕБАЛИ !!!");
        }

        void end(String s) {
            status = Status.PREPARE;
            setContentView(R.layout.endgame);
            TextView finalTextView = findViewById(R.id.textViewEnd);
            finalTextView.setText(s);
        }

        void russianNext() {
            status = Status.RUSSIAN;
            statusTextView.setText("ВАШ ХОД!");
        }

        void hoholNext() {
            status = Status.HOHOL;
            statusTextView.setText("ХОД ХОХЛА!");
            new Handler().postDelayed(() -> {
                gameConstraintLayout = findViewById(R.id.gameConstraintLayout);
                Random rand = new Random();
                int x = 0, y = 0;
                int res = -2;
                boolean isFinding = true;
                while (isFinding) {
                    x = rand.nextInt(10);
                    y = rand.nextInt(10);
                    res = hoholPlayer.ShotToEnemy(russianPlayer, x, y);
                    if (res > -2) isFinding = false;
                }
                ImageView imageView = new ImageView(context);
                imageView.setImageResource(R.drawable.button_border);
                float step = rusGridView.getHeight() / 10f;
                imageView.setColorFilter(Color.WHITE);
                if (res > -1) {
                    imageView.setColorFilter(Color.RED);
                    weDamaged();
                    if (russianPlayer.isLose()) {
                        weLose();
                    }
                }
                gameConstraintLayout.addView(imageView);
                imageView.setLayoutParams(new ViewGroup.LayoutParams( (int) step - 1,  (int) step - 1));
                imageView.setX(rusGridView.getX() + x * step);
                imageView.setY(rusGridView.getY() + y * step);
                if (res > -1) hoholNext();
                else russianNext();
            }, 700);
        }
    }

    public void Play(View view) {
        setContentView(R.layout.app);
        russianPlayer = new Player(10, 10, 10);
        preGridView = findViewById(R.id.gridView);
    }

    public void Fight(View view) {
        gameStart();
    }

    void gameStart() {
        if (russianPlayer.isReady()) {
            hoholPlayer = new Player(10, 10, 10);
            generateRandomShips(hoholPlayer);
            setContentView(R.layout.game);
            game.start(this);
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void random(View view) {
        generateRandomShips(russianPlayer);
        gameStart();
    }

    void generateRandomShips(Player player) {
        Random rand = new Random();
        int life = 4;
        for (int i = 0; i < 10; i++) {
            if (i == 1) life = 3;
            if (i == 3) life = 2;
            if (i == 6) life = 1;
            int x = rand.nextInt(10);
            int y = rand.nextInt(10);
            boolean vertical = rand.nextBoolean();
            int endX = vertical ? x : x + life;
            int endY = vertical ? y + life : y;
            if (!player.setShip(i, x, y, endX, endY, life, vertical)) i--;
        }
    }
}

enum Status {
    PREPARE,
    RUSSIAN,
    HOHOL
}

class Player {
    private final boolean[][] shotGrid;
    private final Ship[] ships;

    Player(int width, int height, int ships) {
        shotGrid = new boolean[width][height];
        this.ships = new Ship[ships];
    }

    boolean setShip(int n, int startX, int startY, int endX, int endY, int life, boolean vertical) {
        if (vertical) {
            if ((startY + life - 1) > 9 || startX > 9) return false;
        } else {
            if ((startX + life - 1) > 9 || startY > 9) return false;
        }
        for (Ship ship : ships) {
            if (ship != null) {
                for (int i = 0; i < life; i++) {
                    if (vertical) {
                        if (ship.hello(startX, startY + i)) return false;
                    } else {
                        if (ship.hello(startX + i, startY)) return false;
                    }
                }
            }
        }
        ships[n] = new Ship(startX, startY, endX, endY, life, vertical);
        return true;
    }

    Ship getShip(int n) {
        return ships[n];
    }

    void delShip(int n) {
        ships[n] = null;
    }

    boolean isReady() {
        for (Ship ship : ships) if (ship == null) return false;
        return true;
    }

    int ShotToEnemy(Player player, int x, int y) {
        if (!shotGrid[x][y]) {
            shotGrid[x][y] = true;
            return player.Shot(x, y);
        }
        return -2;
    }

    int Shot(int x, int y) {
        for (Ship ship : ships) {
            if (ship.Shot(x, y)) return ship.getLife() * 10 + ship.getSize();
        }
        return -1;
    }

    boolean isLose() {
        for (Ship ship : ships) {
            if (ship.getLife() > 0) return false;
        }
        return true;
    }
}

class Ship {
    private final int startX, startY;
    private final int endX, endY;
    private int life;
    private final int size;
    boolean vertical;

    Ship(int startX, int startY, int endX, int endY, int life, boolean vertical) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.life = life;
        this.vertical = vertical;
        size = life;
    }

    int getStartX() {
        return startX;
    }

    int getStartY() {
        return startY;
    }

    int getSize() {
        return size;
    }

    boolean isVertical() {
        return vertical;
    }

    boolean Shot(int x, int y) {
        if (vertical) {
            if (startX == x && y >= startY && y < endY) {
                life--;
                return true;
            }
        } else {
            if (startY == y && x >= startX && x < endX) {
                life--;
                return true;
            }
        }
        return false;
    }

    boolean hello(int x, int y) {
        return vertical ? startX == x && y >= startY && y < endY :
                startY == y && x >= startX && x < endX;
    }

    int getLife() {
        return life;
    }
}