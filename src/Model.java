import java.util.*;

/**
 * будет содержать игровую логику и хранить игровое поле
 */
public class Model {
    // Игровое поле с клетками
    private Tile[][] gameTiles;
    // Ширина игрового поля
    private static final int FIELD_WIDTH = 4;
    // Максимальный вес плитки
    int maxTile = 2;
    // текущий счет
    int score = 0;

    // Переменная для сохранения
    private boolean isSaveNeeded = true;
    // Стек хранения состояния предыдущего игрового поля
    private Stack<Tile[][]> previousStates = new Stack<>();
    // Стек хранения состояния предыдущего счета
    private Stack<Integer> previousScores = new Stack<>();

    public Model() {
        resetGameTiles();
    }

    // Метод для получения игрового поля
    Tile[][] getGameTiles() {
        return gameTiles;
    }

    // Метод перезапуска игра
    void resetGameTiles() {
        gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        // Заполняем игровое поле пустыми клетками (полями)
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                gameTiles[i][j] = new Tile();
            }
        }
        addTile();
        addTile();
    }

    // Метод, который меняет вес клетки на 2 или 4, если она пустая.
    // На 9 двоек должна приходиться 1 четверка
    private void addTile() {
        List<Tile> emptyTiles = getEmptyTiles();
        if (!emptyTiles.isEmpty()) {
            // Выбираем из списка пустых плиток случайную
            int index = (int) (Math.random() * emptyTiles.size()) % emptyTiles.size();
            // Устанавливаем случайной плитке вес 2 или 4
            Tile emptyTile = emptyTiles.get(index);
            emptyTile.value = Math.random() < 0.9 ? 2 : 4;
        }
    }

    // Метод для получения списка пустых клеток на игровом поле
    private List<Tile> getEmptyTiles() {
        // список пустых плиток
        final List<Tile> list = new ArrayList<Tile>();

        for (Tile[] tileArray : gameTiles) {
            for (Tile t : tileArray) {
                if (t.isEmpty()) {
                    list.add(t);
                }
            }
        }
        return list;
    }

    // Метод сжатия (перемещения) плиток, что пустые клетки были у края
    private boolean compressTiles(Tile[] tiles) {
        // Позиция для вставки
        int insertPosition = 0;
        // Если позиции плиток не изменились
        boolean result = false;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            // Если плитка не пустая
            if (!tiles[i].isEmpty()) {
                if (i != insertPosition) {
                    tiles[insertPosition] = tiles[i];
                    tiles[i] = new Tile();
                    // метод изменил положение плиток
                    result = true;
                }
                insertPosition++;
            }
        }
        return result;
    }

    // Метод слияния плиток одного номинала (движение влево)
    private boolean mergeTiles(Tile[] tiles) {
        // Если слияния плиток не было
        boolean result = false;
        LinkedList<Tile> tilesList = new LinkedList<>();
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (tiles[i].isEmpty()) {
                continue;
            }

            // Если 1-ая и 2-ая клетки равны
            if (i < FIELD_WIDTH - 1 && tiles[i].value == tiles[i + 1].value) {
                // Обновленное вес плитки
                int updatedValue = tiles[i].value * 2;
                if (updatedValue > maxTile) {
                    maxTile = updatedValue;
                }
                score += updatedValue;
                // Записываем в конец списка плитку с новым весом
                tilesList.addLast(new Tile(updatedValue));
                // 2-ая клетка равна нулю
                tiles[i + 1].value = 0;
                // Произошло слияние клеток
                result = true;
            } else {
                // Если 1-ая и 2-ая клетки не равны, то добавляем в конец списка 1-ую плитку
                tilesList.addLast(new Tile(tiles[i].value));
            }
            // и обнуляем 1-ую плитку
            tiles[i].value = 0;
        }

        // Записываем обновленный список в текущий
        for (int i = 0; i < tilesList.size(); i++) {
            tiles[i] = tilesList.get(i);
        }

        return result;
    }

    // Метод поворота массива по часовой стрелке
    private Tile[][] rotateClockwise(Tile[][] tiles) {
        final int N = tiles.length;
        Tile[][] result = new Tile[N][N];
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                result[c][N - 1 - r] = tiles[r][c];
            }
        }
        return result;
    }

    // Метод сдвига плиток влево
    public void left() {
        // Проверяем вызывался ли метод сохранения параметров игрового поля
        // за это отвечает поле isSaveNeeded
        if (isSaveNeeded) {
            saveState(gameTiles);
        }
        // флаг - было ли движение
        boolean moveFlag = false;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            // Если плитки передвинулись или слились
            if (compressTiles(gameTiles[i]) | mergeTiles(gameTiles[i])) {
                moveFlag = true;
            }
        }
        // Если движение или слияние было
        if (moveFlag) {
            // добавляем новую плитку
            addTile();
        }
        // Говорим, что нужно сохраниться
        isSaveNeeded = true;
    }

    // Метод сдвига плиток вправо
    public void right() {
        // Сохраняем текущее состояние в стек
        saveState(gameTiles);
        // Поворачиваем массив два раза по часовой стрелки
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        // Сдвигаем все влево
        left();
        // Поворачиваем в исходное положение
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
    }

    // Метод сдвига плиток вверх
    public void up() {
        // Сохраняем текущее состояние в стек
        saveState(gameTiles);
        // Поворачиваем массив три раза по часовой стрелки
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        // Сдвигаем все влево
        left();
        // Поворачиваем в исходное положение
        gameTiles = rotateClockwise(gameTiles);
    }

    // Метод сдвига плиток вниз
    public void down() {
        // Сохраняем текущее состояние в стек
        saveState(gameTiles);
        // Поворачиваем массив один раз по часовой стрелки
        gameTiles = rotateClockwise(gameTiles);
        // Сдвигаем все влево
        left();
        // Поворачиваем в исходное положение
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
        gameTiles = rotateClockwise(gameTiles);
    }

    // Метод возвращающий количество пустых плиток
    private int getEmptyTilesCount() {
        return getEmptyTiles().size();
    }

    // Проверяем заполнено ли игровое поле (нет свободных плит)
    private boolean isFull() {
        return getEmptyTilesCount() == 0;
    }

    // Метод определяющий возможно ли сделать ход так, чтобы состояние
    // игрового поля изменилось
    boolean canMove() {
        // Если на поле еще есть пустые поля
        if (!isFull()) {
            return true;
        }

        for (int x = 0; x < FIELD_WIDTH; x++) {
            for (int y = 0; y < FIELD_WIDTH; y++) {
                Tile t = gameTiles[x][y];
                if ((x < FIELD_WIDTH - 1 && t.value == gameTiles[x + 1][y].value)
                        || ((y < FIELD_WIDTH - 1) && t.value == gameTiles[x][y + 1].value)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Метод для сохранения в стэк состояния поля и счета
    private void saveState(Tile[][] tiles) {
        Tile[][] tempTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                tempTiles[i][j] = new Tile(tiles[i][j].value);
            }
        }

        // Сохраняем текущие состояние поля и счет
        previousStates.push(tempTiles);
        previousScores.push(score);
        // Устанавливаем флаг о сохранении
        isSaveNeeded = false;
    }

    // Метод устанавливает текущее игровое состояние равное последнему в стеках
    public void rollback() {
        if (!previousStates.isEmpty() && !previousScores.isEmpty()) {
            // Восстанавливаем последнее состояние из стека
            gameTiles = previousStates.pop();
            score = previousScores.pop();
        }
    }

    // Рандомный ход при нажатии на кнопку R (нажатие в контроллере)
    public void randomMove() {
        // Случайное число от 0 до 3
        int n = (int) (Math.random() * 4);

        // Случайное число будет соответствовать одному направлению
        switch (n) {
            case 0:
                left();
                break;
            case 1:
                up();
                break;
            case 2:
                down();
                break;
            case 3:
                right();
                break;
        }
    }

    // Метод возвращает объект типа MoveEfficiency описывающий
    // эффективность переданного хода
    private MoveEfficiency getMoveEfficiency(Move move) {
        MoveEfficiency moveEfficiency = new MoveEfficiency(-1, 0, move);
        move.move();
        // Если ход меняет состояние игрового поля
        if (hasBoardChanged()) {
            moveEfficiency = new MoveEfficiency(getEmptyTilesCount(), score, move);
        }
        // Восстанавливаем корректное игровое состояние
        rollback();
        return moveEfficiency;
    }

    // будет возвращать true, в случае, если вес плиток в массиве gameTiles
    // отличается от веса плиток в верхнем массиве стека previousStates
    private boolean hasBoardChanged() {
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                if (gameTiles[i][j].value != previousStates.peek()[i][j].value) {
                    return true;
                }
            }
        }
        return false;
    }

    // Метод, который будет выбирать лучший из возможных ходов и выполнять его.
    void autoMove() {
        // Создаем локальную очередь с параметром Collections.reverseOrder()
        // (для того, чтобы вверху очереди всегда был максимальный элемент)
        // и размером равным четырем.
        PriorityQueue<MoveEfficiency> moveEfficiencies = new PriorityQueue<>(4, Collections.reverseOrder());

        // Заполняем очередь объектами, по одному на каждый ход
        moveEfficiencies.offer(getMoveEfficiency(this::left));
        moveEfficiencies.offer(getMoveEfficiency(this::up));
        moveEfficiencies.offer(getMoveEfficiency(this::right));
        moveEfficiencies.offer(getMoveEfficiency(this::down));

        // Возьмем верхний элемент и выполним ход связанный с ним
        moveEfficiencies.peek().getMove().move();
    }
}
