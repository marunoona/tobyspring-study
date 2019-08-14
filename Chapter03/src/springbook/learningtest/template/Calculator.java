package springbook.learningtest.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {
    public Integer calcSum(String filePath) throws IOException {
//        BufferedReaderCallback callback = new BufferedReaderCallback() {
//            @Override
//            public Integer doSomethingWithReader(BufferedReader br) throws IOException {
//                Integer sum = 0;
//                String line = null;
//                while ((line = br.readLine()) != null) {
//                    sum += Integer.valueOf(line);
//                }
//                return sum;
//            }
//        };
//        return fileReadTemplate(filePath, callback);
        LineCallback sumCallback = new LineCallback<Integer>() {
            @Override
            public Integer doSomethingWithLine(String line, Integer value) {
                return value + Integer.valueOf(line);
            }
        };
        return lineReadTemplate(filePath, sumCallback, 0);
    }

    public Integer calcMultiply(String filePath) throws IOException {
//        BufferedReaderCallback callback = new BufferedReaderCallback() {
//            @Override
//            public Integer doSomethingWithReader(BufferedReader br) throws IOException {
//                Integer multyply = 1;
//                String line = null;
//                while ((line = br.readLine()) != null) {
//                    multyply *= Integer.valueOf(line);
//                }
//                return multyply;
//            }
//        };
//        return fileReadTemplate(filePath, callback);
        LineCallback multiPlyCallback = new LineCallback<Integer>() {
            @Override
            public Integer doSomethingWithLine(String line, Integer value) {
                return value * Integer.valueOf(line);
            }
        };
        return lineReadTemplate(filePath, multiPlyCallback, 1);
    }

    public String concatenate(String filePath) throws IOException {
        LineCallback concateCallback = new LineCallback<String>() {
            @Override
            public String doSomethingWithLine(String line, String value) {
                return value + line;
            }
        };
        return lineReadTemplate(filePath, concateCallback, "");
    }

    public Integer fileReadTemplate(String filePath, BufferedReaderCallback callback) throws
            IOException {
        BufferedReader br = null;
        int ret;
        try {
            br = new BufferedReader(new FileReader(filePath));
            ret = callback.doSomethingWithReader(br);
            return ret;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public <T> T lineReadTemplate(String filePath, LineCallback<T> callback, T initVal) throws
            IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filePath));
            T res = initVal;
            String line = null;
            while ((line = br.readLine()) != null) {
                res = callback.doSomethingWithLine(line, res);
            }
            return res;
        } catch (IOException e) {
            throw e;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
