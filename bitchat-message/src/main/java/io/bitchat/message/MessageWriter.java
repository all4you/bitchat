package io.bitchat.message;

/**
 * <p>
 * A message writer which can save message
 * </p>
 *
 * @author houyi
 */
public interface MessageWriter {

    /**
     * save the message to history storage
     *
     * @param message the message which need to be saved
     */
    void saveHistoryMsg(Message message);

    /**
     * save the message to offline storage
     * when the user is offline so we need to save the message,
     * waiting the user to fetch them once logged online
     *
     * @param message       the message which need to be saved
     * @param offlineUserId the offline user id
     */
    void saveOfflineMsg(Message message, Long offlineUserId);

}
