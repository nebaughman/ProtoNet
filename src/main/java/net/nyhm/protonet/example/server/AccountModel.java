package net.nyhm.protonet.example.server;

import net.nyhm.protonet.example.proto.AccountProto.Account;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * A demonstration user account <em>model</em> (to support testing the framework and protocol messages).
 * Such a model class would be backed by a data store in a typical implementation.
 */
final class AccountModel
{
    private static final Random RAND = new Random(); // just for testing

    /**
     * Map account email addresses to user account objects.
     */
    private final Map<String,UserAccount> mAccounts = new HashMap<>();

    AccountModel()
    {
    }

    /**
     * This method reports whether the specified ID is assigned to any user account.
     */
    private boolean hasId(int id)
    {
        for (UserAccount account : mAccounts.values())
        {
            if (account.getAccount().getId() == id) return true;
        }
        return false;
    }

    /**
     * This method reports whether the named account exists.
     */
    boolean hasUser(String email)
    {
        return mAccounts.containsKey(email);
    }

    /**
     * This method reports whether the specified account exists and has the specified authenticator.
     */
    boolean isAuthenticator(String email, String passHash)
    {
        return hasUser(email) && mAccounts.get(email).isAuthenticator(passHash);
    }

    /**
     * Get an account by email
     */
    Account getAccount(String email)
    {
        UserAccount account = mAccounts.get(email);
        return (account != null) ? account.getAccount() : null;
    }

    /**
     * Insert a new account. The given account's ID is ignored. The new Account is returned with
     * a newly assigned account ID.
     */
    Account insert(Account account, String passHash)
    {
        // creating a new random ID
        int id = 0;
        while (id < 1 || hasId(id)) id = RAND.nextInt(); // Rand.digits(8);
        account = Account.newBuilder().mergeFrom(account).setId(id).build();
        mAccounts.put(account.getEmail(), new UserAccount(account, passHash));
        return account;
    }

    /**
     * Simple struct to demonstrate a server-side user account, including the Account class
     * and its authenticator.
     */
    private static final class UserAccount
    {
        private final Account mAccount;
        private final String mPassHash;

        UserAccount(Account account, String passHash)
        {
            mAccount = account;
            mPassHash = passHash;
        }

        public Account getAccount()
        {
            return mAccount;
        }

        boolean isAuthenticator(String passHash)
        {
            return mPassHash.equals(passHash);
        }
    }
}
