package net.nyhm.protonet.example.server;

import com.google.protobuf.Message;
import net.nyhm.protonet.Peer;
import net.nyhm.protonet.ProtocolHandler;
import net.nyhm.protonet.example.proto.AccountProto.Account;
import net.nyhm.protonet.example.proto.LoginProto;
import net.nyhm.protonet.example.proto.LoginProto.RegisterRequest;
import net.nyhm.protonet.example.proto.LoginProto.RegisterResponse.Decision;
import net.nyhm.protonet.util.Log;

/**
 * Account registration protocol message handler.
 */
final class RegisterHandler extends ProtocolHandler<RegisterRequest>
{
    private final AccountModel mAccounts;

    RegisterHandler(AccountModel accounts)
    {
        super(RegisterRequest.class);
        mAccounts = accounts;
    }

    @Override
    protected void handle(Peer peer, RegisterRequest msg)
    {
        String email = msg.getEmail();
        String pass = msg.getPass(); // TODO: salted hash
        String name = msg.getName();

        Message response;

        if (mAccounts.hasUser(email))
        {
            response = LoginProto.RegisterResponse.newBuilder()
                .setDecision(Decision.DECLINED)
                .setMessage("Account already exists")
                .build();

            Log.info("Account exists " + email);
        }
        else
        {
            // model will assign account ID upon insertion
            Account account = Account.newBuilder()
                .setEmail(email)
                .setName(name)
                .build();

            // get new account instance including ID
            account = mAccounts.insert(account, pass);

            response = LoginProto.RegisterResponse.newBuilder()
                .setDecision(Decision.ACCEPTED)
                .setMessage("Account created")
                .setAccount(account)
                .build();

            Log.info("Registering account " + account.getEmail());
        }

        peer.send(response);
    }
}
