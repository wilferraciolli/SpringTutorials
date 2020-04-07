package com.wiltech.azurewebhook.subscription.creator;


import com.microsoft.azure.credentials.ApplicationTokenCredentials;
import com.microsoft.azure.eventgrid.EventGridClient;
import com.microsoft.azure.eventgrid.TopicCredentials;
import com.microsoft.azure.eventgrid.implementation.EventGridClientImpl;
import com.microsoft.azure.eventgrid.models.EventGridEvent;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.EventHubRuntimeInformation;
import com.microsoft.azure.eventhubs.EventPosition;
import com.microsoft.azure.eventhubs.PartitionReceiver;
import com.microsoft.azure.management.eventgrid.v2018_01_01.EventHubEventSubscriptionDestination;
import com.microsoft.azure.management.eventgrid.v2018_01_01.EventSubscription;
import com.microsoft.azure.management.eventgrid.v2018_01_01.EventSubscriptionFilter;
import com.microsoft.azure.management.eventgrid.v2018_01_01.Topic;
import com.microsoft.azure.management.eventgrid.v2018_01_01.implementation.EventGridManager;
import com.microsoft.azure.management.eventhub.EventHub;
import com.microsoft.azure.management.eventhub.EventHubAuthorizationRule;
import com.microsoft.azure.management.eventhub.EventHubNamespace;
import com.microsoft.azure.management.eventhub.EventHubNamespaceSkuType;
import com.microsoft.azure.management.eventhub.implementation.EventHubManager;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import com.microsoft.azure.management.resources.fluentcore.utils.SdkContext;
import com.microsoft.azure.management.resources.implementation.ResourceManager;
import com.microsoft.rest.LogLevel;
import org.joda.time.DateTime;

import java.io.File;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class CreateSubscription {

    private static ResourceManager resourceManager;
    private static EventHubManager eventHubManager;
    private static EventGridManager eventGridManager;
    private static EventGridClient eventGridClient;

    public static void main(String[] args) {

//        final String rgName = SdkContext.randomResourceName("rgeventgrid", 24);
//        final String eventHubNamespaceName = SdkContext.randomResourceName("ehns", 24);
//        final String eventHubRuleName = "ehRule1";
//        final String topicName = SdkContext.randomResourceName("topicsample", 24);
//        final String eventSubscriptionName = "EventSubscription1";
//        final String defaultRegion = Region.US_WEST.label();

        //
        System.out.println("Creating an Azure EventGrid topic");

        System.out.println(eventGridManager.topics().list());

       // System.out.println("EventGrid topic created with name " + eventGridTopic.name());

        //============================================================
        // Create an event grid subscription.
        //
        System.out.println("Creating an Azure EventGrid Subscription");

//        EventSubscription eventSubscription = eventGridManager.eventSubscriptions().define("my-event_sub")
//                .withScope(eventGridTopic.id())
//                .withDestination(new EventHubEventSubscriptionDestination()
//                        .withResourceId(eventHub.id()))
//                .withFilter(new EventSubscriptionFilter()
//                        .withIsSubjectCaseSensitive(false)
//                        .withSubjectBeginsWith("")
//                        .withSubjectEndsWith(""))
//                .create();
//
//        System.out.println("EventGrid event subscription created with name " + eventSubscription.name());
    }

    private static void createTopic(){

        }

}
