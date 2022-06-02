package es.urjc.etsii.grafo.iudex.services.events.types;

import es.urjc.etsii.grafo.iudex.entities.JudgeEvent;

public class ExecutionEndedEvent extends JudgeEvent {
        private final long executionTime;
    
        /**
         * Create a new ExecutionEndedEvent providing the accumulated execution time.
         *
         * @param executionTime total execution time in nanoseconds
         */
        public ExecutionEndedEvent(long executionTime) {
            this.executionTime = executionTime;
        }
    
        /**
         * Get accumulated execution time
         *
         * @return execution time in nanoseconds
         */
        public long getExecutionTime() {
            return executionTime;
        }
}
