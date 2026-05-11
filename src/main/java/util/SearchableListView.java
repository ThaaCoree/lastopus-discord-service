package util;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class SearchableListView {

    public static void makeSearchable(ListView<String> listView, ObservableList<String> originalItems, TextField textField) {
        // ใช้ FilteredList เพื่อกรองรายการ
        FilteredList<String> filteredItems = new FilteredList<>(originalItems, s -> true);
        listView.setItems(filteredItems);

        textField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;
            String filter = newVal.toLowerCase();
            filteredItems.setPredicate(item -> item.toLowerCase().contains(filter));
        });
    }
}
