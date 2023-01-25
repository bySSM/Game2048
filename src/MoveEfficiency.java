/**
 * Класс, описывающий эффективность хода
 */

class MoveEfficiency implements Comparable<MoveEfficiency> {
    // Количество пустых плит
    private int numberOfEmptyTiles;
    // Количество очков
    private int score;
    // Поле хода
    private Move move;

    public MoveEfficiency(int numberOfEmptyTiles, int score, Move move) {
        this.numberOfEmptyTiles = numberOfEmptyTiles;
        this.score = score;
        this.move = move;
    }

    // Метод для сравнения
    @Override
    public int compareTo(MoveEfficiency o) {
        // Сравниваем по количеству пустых плит
        if (numberOfEmptyTiles > o.numberOfEmptyTiles) {
            return 1;
        } else if (numberOfEmptyTiles < o.numberOfEmptyTiles) {
            return -1;
            // Если равно количество пустых плит, то сравнивание по счету
        } else {
            if (score > o.score) {
                return 1;
            } else if (score < o.score) {
                return -1;
            }
        }
        // Если счет и количество пустых плит равны, то считаем эффективность
        // ходов равной и возвращаем 0
        return 0;
    }

    public Move getMove() {
        return move;
    }
}