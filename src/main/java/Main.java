


import org.sikuli.script.*;

public class Main {

    public static void main(String[] args) throws Exception {
        String projectPath = "C:\\Users\\Evgeny\\Desktop\\sikuli\\src\\main\\resources\\";
        App app = new App("C:\\sikuli\\Test_Project_3.3.exe");
        app.open();
        Region region = app.window();
        app.focus();


        Region regNotActiveCheckbox = region.find(projectPath + "checkboxnotactive.PNG");
        Region regExitCheckboxActive = region.find(new Pattern(projectPath + "exitCheckboxActiveNotChecked.PNG"));

        //Без задержек форма не успевает отрисовать новое состояние
        Thread.sleep(1_000);
        Region regCheckbox = new FindUtil().findWithColorSensitive(regExitCheckboxActive, new Pattern(projectPath + "checkbox.png").exact());
        Thread.sleep(1_000);

        //unsupportedCheckboxMask является плохим примером масок для чекбокса. Из-за нее sikuli производит некорректный поиск изображения
        //checkboxMask пример хорошей маски
        regExitCheckboxActive.find(new Pattern(projectPath + "checkbox.png").exact().mask(projectPath + "unsupportedCheckboxMask.png")).hover();
        Thread.sleep(10_000);
        regCheckbox.hover();
        regCheckbox.click();
        regNotActiveCheckbox.hover();
        regNotActiveCheckbox.click();
        Thread.sleep(1_000);
        new FindUtil().findWithColorSensitive(regExitCheckboxActive, new Pattern(projectPath + "checkboxDisabled.png").exact(), new Pattern(projectPath + "checkboxMask.png")).hover();
        //Здесь тест должен упасть, чекбоксы не совпадают по цвету
        new FindUtil().findWithColorSensitive(regExitCheckboxActive, new Pattern(projectPath + "checkbox.png").exact(), new Pattern(projectPath + "checkboxMask.png")).hover();

        //findWithColorSensitive() необязателен. Sikuli чувствует разницу между цветами изображения(при точности поиска более 0.95)
    }

}
