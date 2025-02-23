package rpg.rpg_base.Data;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Util {

    public Component formatYmlString(String str) {
        List<String> formatableHashes = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "f");
        Component formattedComponent = Component.text("");
        formattedComponent = formattedComponent.decoration(TextDecoration.ITALIC, TextDecoration.State.NOT_SET);

        String[] words = str.split(" ");
        TextColor currentColor = null;
        Set<TextDecoration> decorations = new HashSet<>();

        for (String word : words) {
            StringBuilder currentText = new StringBuilder();

            boolean inFormatting = false;
            StringBuilder colorCode = new StringBuilder();

            for (int i = 0; i < word.length(); i++) {
                char ch = word.charAt(i);

                if (ch == '&') {
                    if (currentText.length() > 0) {
                        Component tempComponent = Component.text(currentText.toString());

                        if (currentColor != null) {
                            tempComponent = tempComponent.color(currentColor);
                        }
                        for (TextDecoration decoration : decorations) {
                            tempComponent = tempComponent.decorate(decoration);
                        }
                        formattedComponent = formattedComponent.append(tempComponent);
                        currentText = new StringBuilder();
                    }
                    if (i + 1 < word.length() && word.charAt(i + 1) == '<') {
                        decorations = new HashSet<>();
                        currentColor = null;

                        inFormatting = true;
                        i++; // Skip '<'
                    }
                    continue;
                }

                if (inFormatting) {
                    if (ch == '>') {
                        inFormatting = false;
                        if (colorCode.length() == 6) {
                            currentColor = TextColor.color(Integer.parseInt(colorCode.toString(), 16));
                        }
                        colorCode.setLength(0);
                        continue;
                    }

                    switch (ch) {
                        case '*' -> decorations.add(TextDecoration.BOLD);
                        case '/' -> decorations.add(TextDecoration.ITALIC);
                        case '-' -> decorations.add(TextDecoration.STRIKETHROUGH);
                        case '_' -> decorations.add(TextDecoration.UNDERLINED);
                        case '#' -> colorCode = new StringBuilder();
                        default -> {
                            if (formatableHashes.contains(String.valueOf(ch))) {
                                colorCode.append(ch);
                            }
                        }
                    }
                    continue;
                }

                currentText.append(ch);
            }

            if (!currentText.isEmpty()) {
                Component tempComponent = Component.text(currentText.toString());
                tempComponent = tempComponent.decoration(TextDecoration.ITALIC, TextDecoration.State.NOT_SET);
                if (currentColor != null) {
                    tempComponent = tempComponent.color(currentColor);
                }
                for (TextDecoration decoration : decorations) {
                    tempComponent = tempComponent.decorate(decoration);
                }
                formattedComponent = formattedComponent.append(tempComponent);
            }

            formattedComponent = formattedComponent.append(Component.text(" "));
        }

        return formattedComponent;
    }
}
