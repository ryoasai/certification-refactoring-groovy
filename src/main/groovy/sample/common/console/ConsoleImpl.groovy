package sample.common.console

import java.text.ParseException
import java.text.SimpleDateFormat
import org.apache.commons.lang.ObjectUtils
import org.springframework.stereotype.Component
import sample.common.entity.Identifiable
import sample.common.entity.NameId

@Component
class ConsoleImpl implements Console {

    static final String DEFAULT_PROMPT_STRING = '>'
    private String promptString = DEFAULT_PROMPT_STRING

    String getPromptString() {
        return promptString
    }

    void setPromptString(String promptString) {
        this.promptString = promptString
    }


    @Override
    void display(List<String> messages) {
        for (String line in messages) {
            println line // 機能一覧の表示
        }
    }

    @Override
    void display(String... messages) {
        for (String line in messages) {
            println line // 機能一覧の表示
        }
    }

    @Override
    boolean confirm(String message, String yes, String no) {
        while (true) {
            String input = accept('\n' + message + '\n[' + yes + ',' + no + ']' + promptString)
            if (yes.equals(input)) return true
            if (no.equals(input)) return false

            display('\n' + yes + 'か' + no + 'を入力してください。')
        }
    }

    @Override
    String accept(String message) {
        println message
        print promptString

        return doAcceptChars()
    }

    @Override
    String accept(String message, Closure validInput) {
        while (true) {
            // 正しく入力されるまでループ
            String input = accept(message);

            try {
                if (validInput.call(input)) return input
            } catch (Exception ex) {
                // TODO 例外処理の場所の検討
                // もともとのコードを動きを一旦保持。
                ex.printStackTrace()
            }
        }
    }

    @Override
    int acceptInt(String message) {
        while (true) {
            try {
                String input = accept(message);

                return Integer.parseInt(input)
            } catch (NumberFormatException e) {
                e.printStackTrace()
            }
        }
    }


    @Override
    int acceptInt(String message, Closure validInput) {
        while (true) {
            // 正しく入力されるまでループ
            int input = acceptInt(message);

            try {
                if (validInput.call(input)) return input
            } catch (Exception ex) {
                // TODO 例外処理の場所の検討
                // もともとのコードを動きを一旦保持。
                ex.printStackTrace()
            }
        }
    }


    @Override
    long acceptLong(String message) {
        while (true) {
            try {
                String input = accept(message);

                return Long.parseLong(input)
            } catch (NumberFormatException e) {
                e.printStackTrace()
            }
        }
    }

    @Override
    long acceptLong(String message, Closure validInput) {
        while (true) {
            // 正しく入力されるまでループ
            long input = acceptLong(message);

            try {
                if (validInput.call(input)) return input
            } catch (Exception ex) {
                // TODO 例外処理の場所の検討
                // もともとのコードを動きを一旦保持。
                ex.printStackTrace()
            }
        }
    }


    @Override
    Date acceptDate(String message) {
        acceptDate(message, 'yyyyMMdd')
    }


    @Override
    Date acceptDate(String message, String format) {
        while (true) {
            String input = accept(message);
            SimpleDateFormat dateFormat = new SimpleDateFormat(format)
            try {
                return dateFormat.parse(input)
            } catch (ParseException e) {
                e.printStackTrace()
            }
        }
    }


    @Override
    String acceptFromNameIdList(List<? extends NameId<?>> selectList, String message) {
        String result = null
        while (true) {
            println message
            print nameIdListToString(selectList) + promptString

            result = doAcceptChars()
            if (isValidId(selectList, result)) {
                return result
            }
        }
    }

    private boolean isValidId(List<? extends Identifiable<?>> idList, String id) {
        for (Identifiable<?> identifiable: idList) {
            if (id.equals(ObjectUtils.toString(identifiable.getId()))) {
                return true
            }
        }

        false
    }


    private String nameIdListToString(List<? extends NameId<?>> nameIdList) {
        StringBuilder buff = new StringBuilder()
        for (NameId<?> partner: nameIdList) {
            buff.append(partner.getId() + ' ' + partner.getName())
            buff.append('\n')
        }

        buff.append(' ['); // IDリストの表示
        for (NameId<?> partner: nameIdList) {
            buff.append(partner.getId())
            buff.append(',')
        }
        buff.deleteCharAt(buff.length() - 1); // 末尾の','を削除
        buff.append(']')

        buff.toString()
    }

    @Override
    String acceptFromIdList(List<? extends Identifiable<?>> selectList, String message) {

        String result = null
        while (true) {
            println(message)
            print(idListToString(selectList) + promptString)

            result = doAcceptChars()
            if (isValidId(selectList, result)) {
                return result
            }
        }
    }


    private String idListToString(List<? extends Identifiable<?>> idList) {
        StringBuilder buff = new StringBuilder()
        for (Identifiable<?> partner: idList) {
            buff.append(partner.getId())
            buff.append('\n')
        }

        buff.append(' ['); // IDリストの表示
        for (Identifiable<?> partner: idList) {
            buff.append(partner.getId())
            buff.append(',')
        }
        buff.deleteCharAt(buff.length() - 1); // 末尾の','を削除
        buff.append(']')

        buff.toString()
    }

    @Override
    String acceptFromList(List<String> selectList, String message) {
        String result = null
        while (true) {
            println message
            print selectList.toString() + promptString

            result = doAcceptChars()
            if (selectList.contains(result)) return result
        }
    }

    /**
     * キーボードからの入力受取り
     *
     * @return 入力文字列
     */
    private String doAcceptChars() {
        new BufferedReader(new InputStreamReader(System.in)).readLine()
    }
}
