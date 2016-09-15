package com.madmensoftware.www.pops.Helpers;

import android.os.AsyncTask;

import com.madmensoftware.www.pops.Models.User;
import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Charge;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by carsonjones on 9/15/16.
 */
//:TODO Create reusable payment class that takes user objects, job object, and price and does different transactions
public class StripePaymentHelper {


    private class CreateChargeTask extends AsyncTask<User, Integer, String> {
        // Do the long-running work in here
        protected String doInBackground(User... params) {
            User user = params[0];

            Stripe.apiKey = "sk_test_I9UFP4mZBd3kq6W7w9zDenGq";
            // Create a charge: this will charge the user's card
            try {
                Map<String, Object> chargeParams = new HashMap<String, Object>();
                chargeParams.put("amount", 1000); // Amount in cents
                chargeParams.put("currency", "usd");
                chargeParams.put("customer", user.getStripeCustomerId());
                chargeParams.put("description", "Example charge");
                chargeParams.put("destination", "acct_18u1uFHfcGmL46Ma");
                chargeParams.put("application_fee", 250);

                Charge charge = Charge.create(chargeParams);
            } catch (CardException e) {
                // The card has been declined
            } catch (APIException e) {
                e.printStackTrace();
            } catch (InvalidRequestException e) {
                e.printStackTrace();
            } catch (APIConnectionException e) {
                e.printStackTrace();
            } catch (AuthenticationException e) {
                e.printStackTrace();
            }

            return "";
        }

        // This is called each time you call publishProgress()
        protected void onProgressUpdate(Integer... progress) {

        }

        // This is called when doInBackground() is finished
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }
}
