package net.nyhm.protonet.example.server;

import net.nyhm.protonet.Peer;
import net.nyhm.protonet.ProtocolHandler;
import net.nyhm.protonet.example.proto.AccountProto.Account;
import net.nyhm.protonet.example.proto.LoginProto;
import net.nyhm.protonet.example.proto.LoginProto.LoginRequest;
import net.nyhm.protonet.example.proto.LoginProto.LoginResponse;
import net.nyhm.protonet.example.proto.LoginProto.LoginResponse.Builder;
import net.nyhm.protonet.example.proto.LoginProto.LoginResponse.Decision;
import net.nyhm.protonet.util.Log;

/**
 * Account login protocol message handler.
 */
final class LoginHandler extends ProtocolHandler<LoginRequest>
{
    private final AccountModel mAccounts;

    LoginHandler(AccountModel accounts)
    {
        super(LoginRequest.class);
        mAccounts = accounts;
    }

    @Override
    public void handle(Peer peer, LoginProto.LoginRequest msg)
    {
        String email = msg.getEmail();
        String pass = msg.getPass(); // TODO: salted hash

        Decision decision;
        String message;
        Account account;

        if (!mAccounts.hasUser(email))
        {
            decision = Decision.REGISTER; // unknown user must supply email to register
            message = "Please register";
            account = null;
            Log.info("No such account: " + email);
        }
        else if (!mAccounts.isAuthenticator(email, pass))
        {
            decision = Decision.DECLINED;
            message = "Invalid login";
            account = null;
            Log.info("Invalid login: " + email);
        }
        else
        {
            decision = Decision.ACCEPTED;
            message = "Welcome back";
            account = mAccounts.getAccount(email);
            Log.info("Valid login: " + email);
        }

        Builder response = LoginResponse.newBuilder()
            .setDecision(decision)
            .setMessage(message);

        if (account != null) response.setAccount(account);

        peer.send(response.build());
    }
}

