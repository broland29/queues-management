package gui;

public enum TextFieldNames {
    NUMBER_OF_CLIENTS{
        @Override
        public String toString() {
            return "number of clients";
        }
    },
    NUMBER_OF_SERVERS{
        @Override
        public String toString() {
            return "number of servers";
        }
    },
    TIME_LIMIT{
        @Override
        public String toString() {
            return "time limit";
        }
    },
    MIN_ARRIVAL_TIME{
        @Override
        public String toString() {
            return "minimum arrival time";
        }
    },
    MAX_ARRIVAL_TIME{
        @Override
        public String toString() {
            return "maximum arrival time";
        }
    },
    MIN_PROCESSING_TIME{
        @Override
        public String toString() {
            return "minimum processing time";
        }
    },
    MAX_PROCESSING_TIME{
        @Override
        public String toString() {
            return "maximum processing time";
        }
    }
}
