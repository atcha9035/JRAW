package net.dean.jraw.test.auth;

import net.dean.jraw.ApiException;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.managers.InboxManager;
import net.dean.jraw.models.Message;
import net.dean.jraw.paginators.InboxPaginator;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class InboxManagerTest extends AuthenticatedRedditTest {
    private InboxManager inbox;

    public InboxManagerTest() {
        this.inbox = new InboxManager(reddit);
    }

    @Test
    public void testRead() {
        try {
            Message m1 = getFirstNonCommentMessage();
            boolean expected = !m1.isRead();
            inbox.setRead(m1, expected);
            Message m2 = getFirstNonCommentMessage();
            assertEquals(m1.getFullName(), m2.getFullName());
            assertEquals(m2.isRead().booleanValue(), expected);
        } catch (NetworkException e) {
            handle(e);
        } catch (IllegalStateException e) {
            // e.getCause() might be a NetworkException
            handle(e.getCause() != null ? e.getCause() : e);
        }
    }

    @Test
    public void testCompose() {
        try {
            String subject = getUserAgent(InboxManagerTest.class);
            String body = "epoch=" + epochMillis();
            inbox.compose("/r/jraw_testing2", subject, body);
        } catch (NetworkException | ApiException e) {
            handle(e);
        }
    }

    private Message getFirstNonCommentMessage() {
        InboxPaginator paginator = inbox.iterate(InboxPaginator.Where.INBOX);
        for (Message m : paginator.next()) {
            if (!m.isComment()) {
                return m;
            }
        }

        return null;
    }
}