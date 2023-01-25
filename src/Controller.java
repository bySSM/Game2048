import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * будет следить за нажатием клавиш во время игры
 */
public class Controller extends KeyAdapter {
    private Model model;
    private View view;

    // Вес плитки, при достижении которой игра будет выигранной
    private static final int WINNING_TILE = 2048;

    public Controller(Model model) {
        // Сохраняем в поле view новый объект с текущим controller
        view = new View(this);
        this.model = model;
    }

    public View getView() {
        return view;
    }

    // Метод возвращающий игровое поле у модели
    public Tile[][] getGameTiles() {
        return model.getGameTiles();
    }

    // Метод возвращающий текущий счет из модели
    public int getScore() {
        return model.score;
    }

    // Метод для обработки действий пользователя (нажатие клавиш)

    @Override
    public void keyPressed(KeyEvent e) {
        // Если нажата клавиша Escape
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            resetGame();
        }

        // Если метод canMove модели возвращает false - игра проиграна
        if (!model.canMove()) {
            view.isGameLost = true;
        }

        // Если игра активна и пользователь нажимает на одну из клавиш
        if (!view.isGameLost && !view.isGameWon) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                model.left();
            }

            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                model.right();
            }

            if (e.getKeyCode() == KeyEvent.VK_UP) {
                model.up();
            }

            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                model.down();
            }

            // Отмена последнего хода
            if (e.getKeyCode() == KeyEvent.VK_Z) {
                model.rollback();
            }

            // Для вызова случайного хода
            if (e.getKeyCode() == KeyEvent.VK_R) {
                model.randomMove();
            }

            // Для вызова автоматического (умного) хода
            if (e.getKeyCode() == KeyEvent.VK_A) {
                model.autoMove();
            }
        }

        // Если вес плитки на поле равен максимальному значению - игра выиграна
        if (model.maxTile == WINNING_TILE) {
            view.isGameWon = true;
        }

        view.repaint();
    }

    // Метод, который возвращает игровое поле в начальное состояние
    public void resetGame() {
        // Обнуляем счет
        model.score = 0;
        // устанавливаем флаги
        view.isGameWon = false;
        view.isGameLost = false;
        // перезапускаем игровое поле
        model.resetGameTiles();
    }
}