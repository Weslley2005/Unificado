package br.unigran.tcc.ViewModel;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class TelefoneMask {

    public static TextWatcher inserir(final EditText editText) {
        return new TextWatcher() {
            boolean isUpdating;
            String mask = "(##) #####-####";
            String oldText = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = unmask(s.toString());
                String mascara = "";

                if (isUpdating) {
                    oldText = str;
                    isUpdating = false;
                    return;
                }

                int i = 0;
                for (char m : mask.toCharArray()) {
                    if (m != '#' && str.length() > oldText.length()) {
                        mascara += m;
                        continue;
                    }
                    try {
                        mascara += str.charAt(i);
                    } catch (Exception e) {
                        break;
                    }
                    i++;
                }

                isUpdating = true;
                editText.setText(mascara);
                editText.setSelection(mascara.length());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
    }

    private static String unmask(String s) {
        return s.replaceAll("[^0-9]", "");
    }
}
