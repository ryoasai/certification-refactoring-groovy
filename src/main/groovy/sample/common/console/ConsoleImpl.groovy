package sample.common.console

import java.text.ParseException
import java.text.SimpleDateFormat
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

    int acceptFromMenuItems(Map<String, String> menuMap, int maxWidth = 80) {
        StringBuilder buf = new StringBuilder()
        menuMap.eachWithIndex {key, value, i ->
            def item = "${i + 1}.${value}"
            buf << item << ' '
        }

        display wrap(buf.toString(), maxWidth)
        acceptInt("[1-${menuMap.size()}]>") {it ->
            if (it < 1 || it > menuMap.size()) { // 項目番号入力エラー
                display '項目番号の入力が正しくありません。'
                return false
            }

            true
        }
    }

    private def wrap(text, maxWidth = 80) {
        def line = new StringBuilder()
        def allLines = []

        text.eachMatch(/\S+/) { item ->
            if (line.size() + 8 + item.size() > maxWidth) {
                allLines << line.toString()
                line = new StringBuilder()
            }

            line << (line.length() == 0 ? item : '\t' + item)
        }

        allLines << line.toString()

        allLines.join('\n')
    }


    @Override
    int acceptInt(String message) {
        while (true) {
            try {
                String input = accept(message);

                return input.toInteger()
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

                return input.toLong()
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
        doAcceptFromIdList(selectList, message)
    }

    @Override
    String acceptFromIdList(List<? extends Identifiable<?>> selectList, String message) {
        doAcceptFromIdList(selectList, message)
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

    private boolean isValidId(List itemList, String id) {
        itemList.any {
            it.id?.toString() == id
        }
    }

    private String doAcceptFromIdList(List selectList, String message) {

        String result = null
        while (true) {
            println(message)
            print(listToString(selectList) + promptString)

            result = doAcceptChars()
            if (isValidId(selectList, result)) {
                return result
            }
        }
    }


    private String listToString(itemList) {
        def sb = new StringBuilder()
        itemList.each {
            sb << it.id
            if (it instanceof NameId) {
                sb << ' '
                sb << it.name
            }
            sb << '\n'
        }

        sb << ' [' // IDリストの表示
        itemList.each {
            sb << it.id
           sb << ','
        }
        sb.deleteCharAt(sb.length() - 1); // 末尾の','を削除
        sb << ']'

        sb.toString()
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
