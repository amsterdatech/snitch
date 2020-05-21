package br.com.beblue.snitchdemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import br.com.beblue.snitch.core.snitch
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        purchase_button.setOnClickListener {
            snitch(this)
                .click {
                    customerId = UUID.randomUUID().toString()

                    "category" to "crm-users"
                    "action" to "click-btn-purchase"
                    "label" to "purchase-now"
                    "CLICK CUSTOM PROPERTY" to "CLICK CUSTOM VALUE"

                }

            snitch(this)
                .track {
                    customerId = UUID.randomUUID().toString()

                    "taxId" to "10677068794"
                    "action" to "purchase_item"

                    name
                }

            snitch(this)
                .pushReceived {
                    notificationId = "3950c362-8b97-4054-8391-f65166e0d3b4"
                    pushType = "voucher"
                    title = "Vem que tem cashback exlusivo"
                    message = "O EC está com ofertas exclusivas para você"
                    image = ""
                    deepLink = "anyString"
                    emoji = ""
                    customerId = UUID.randomUUID().toString()

                    "click_custom_property" to "click_custom_value"
                    "push_custom_property" to "push_custom_value"
                }
//
//            snitch(this)
//                .pushClicked {
//                    notificationId = "3950c362-8b97-4054-8391-f65166e0d3b4"
//                    pushType = "credit"
//                    title = "Vem que tem cashback exlusivo"
//                    message = "O EC está com ofertas exclusivas para você"
//                    image = ""
//                    deepLink = "anyString"
//                    emoji = ""
//                    customerId = UUID.randomUUID().toString()
//                }
//
//
//            snitch(this)
//                .pushDismissed {
//                    notificationId = "3950c362-8b97-4054-8391-f65166e0d3b4"
//                    pushType = "merchant"
//                    title = "Vem que tem cashback exlusivo"
//                    message = "O EC está com ofertas exclusivas para você"
//                    image = ""
//                    deepLink = "anyString"
//                    emoji = ""
//                    customerId = UUID.randomUUID().toString()
//                }
        }
    }

    override fun onStart() {
        super.onStart()

//        snitch(this)
//            .screen {
//                screenName(MainActivity::class.java.simpleName.snakeCase())
//            }

        snitch(this)
            .device { }
    }
}
