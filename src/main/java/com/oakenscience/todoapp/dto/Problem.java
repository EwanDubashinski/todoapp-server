package com.oakenscience.todoapp.dto;

public class Problem {
    enum ProblemType {
        OBJECT("object"),
        FIELD("field");
        private final String type;

        ProblemType(String type) {
            this.type = type;
        }
        public String getType() {
            return type;
        }

        @Override
        public String toString() {
            return type;
        }
    }
    private ProblemType type;
    private String field;
    private String message;

    public Problem() {
    }

    public Problem(String message) {
        this.type = ProblemType.OBJECT;
        this.message = message;
    }

    public Problem(String field, String message) {
        this.type = ProblemType.FIELD;
        this.field = field;
        this.message = message;
    }

    public ProblemType getType() {
        return type;
    }

    public void setType(ProblemType type) {
        this.type = type;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
