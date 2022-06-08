package es.urjc.etsii.grafo.iudex.services.events.types;

import org.jvnet.hk2.annotations.Service;

import es.urjc.etsii.grafo.iudex.entities.JudgeEvent;

@Service
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
