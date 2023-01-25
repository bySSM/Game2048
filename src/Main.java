import javax.swing.*;

/**
 * будет содержать только метод main и служить точкой входа в наше приложение
 */
public class Main {
    public static void main(String[] args) {
        Model model = new Model();
        Controller controller = new Controller(model);
        JFrame game = new JFrame();

        // Название окна
        game.setTitle("2048");
        // Закрытие по при нажатии на красный крестик окна
        game.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // Размер окна
        game.setSize(450, 500);
        // Запрещаем изменение окна
        game.setResizable(false);

        // Добавляем контролер
        game.add(controller.getView());


        // Устанавливаем окно в центр экрана
        game.setLocationRelativeTo(null);
        // Делаем окно видимым
        game.setVisible(true);

    }
}
