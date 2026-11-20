package com.agenticqa.agents;

import com.agenticqa.agents.llm.LlmConfig;
import com.agenticqa.agents.output.AgentOutputWriter;
import com.agenticqa.agents.pipeline.Agent;
import com.agenticqa.agents.pipeline.PipelineContext;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.Body;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.Message;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;

/**
 * Sends the analysis report via AWS SES when enabled.
 */
public final class EmailReportSenderAgent implements Agent {

    private final LlmConfig config;
    private final AgentOutputWriter writer;

    public EmailReportSenderAgent(LlmConfig config, AgentOutputWriter writer) {
        this.config = config;
        this.writer = writer;
    }

    @Override
    public String name() {
        return "email-report-sender";
    }

    @Override
    public void run(PipelineContext context) throws Exception {
        boolean enabled = Boolean.parseBoolean(config.get("REPORT_EMAIL_ENABLED"));
        String to = config.get("SES_TO_EMAIL");
        String from = config.get("SES_FROM_EMAIL");
        String report = context.artifact("reportAnalysis");
        if (report.isBlank()) {
            report = context.artifact("requirementAnalysis");
        }

        if (!enabled) {
            writer.write("10-email-status.txt", "Email skipped: REPORT_EMAIL_ENABLED=false");
            context.putMeta("emailSent", false);
            return;
        }
        if (to.isBlank() || from.isBlank()) {
            writer.write("10-email-status.txt", "Email skipped: SES_TO_EMAIL / SES_FROM_EMAIL not set");
            context.putMeta("emailSent", false);
            return;
        }

        String accessKey = config.require("AWS_ACCESS_KEY_ID");
        String secret = config.require("AWS_SECRET_ACCESS_KEY");
        String region = config.get("AWS_REGION");
        if (region.isBlank()) {
            region = "ap-southeast-2";
        }

        String subject = "Agentic QA Report — " + context.outputDir().getFileName();
        String bodyText = report;

        try (SesClient ses = SesClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secret)))
                .build()) {

            SendEmailRequest request = SendEmailRequest.builder()
                    .source(from)
                    .destination(Destination.builder().toAddresses(to.split(",")).build())
                    .message(Message.builder()
                            .subject(Content.builder().data(subject).charset("UTF-8").build())
                            .body(Body.builder()
                                    .text(Content.builder().data(bodyText).charset("UTF-8").build())
                                    .build())
                            .build())
                    .build();

            String messageId = ses.sendEmail(request).messageId();
            writer.write("10-email-status.txt", "Email sent. messageId=" + messageId + " to=" + to);
            context.putMeta("emailSent", true);
            context.putMeta("emailMessageId", messageId);
        }
    }
}
