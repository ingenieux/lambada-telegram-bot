package io.ingenieux.lambada.bot;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import javax.inject.Singleton;

public class ContextModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Context.class).toInstance(FAKE_CONTEXT);
    }

    private static final Context FAKE_CONTEXT = new Context() {
        @Override
        public String getAwsRequestId() {
            return null;
        }

        @Override
        public String getLogGroupName() {
            return null;
        }

        @Override
        public String getLogStreamName() {
            return null;
        }

        @Override
        public String getFunctionName() {
            return null;
        }

        @Override
        public String getFunctionVersion() {
            return null;
        }

        @Override
        public String getInvokedFunctionArn() {
            return null;
        }

        @Override
        public CognitoIdentity getIdentity() {
            return null;
        }

        @Override
        public ClientContext getClientContext() {
            return null;
        }

        @Override
        public int getRemainingTimeInMillis() {
            return 0;
        }

        @Override
        public int getMemoryLimitInMB() {
            return 0;
        }

        @Override
        public LambdaLogger getLogger() {
            return FAKE_LOGGER;
        }
    };

    private static final LambdaLogger FAKE_LOGGER = new LambdaLogger() {
        @Override
        public void log(String msg) {
            System.err.println(msg);
        }
    };
}
