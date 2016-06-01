/**
 * Created by kreatora3 on 4/30/2016.
 */
window.onload = function(){

    var number = document.getElementById("in")
    var line1 = document.getElementById("l1")
    var line1number = document.getElementById("ln1")
    var line2 = document.getElementById("l2")
    var line2number = document.getElementById("ln2")
    var total = document.getElementById("t")
    var text = ""


    var buttonSP = document.getElementById("sp")
//calls activity with bluetooth devices list
      buttonSP.addEventListener('click', function(){

       Android.ScanForPrinters();
    })

    var buttonP = document.getElementById("p")
    // calls print function
    buttonP.addEventListener('click', function(){
            text = text + number.value + '\n'
            text = text + line1.value  + '\n'
            text = text + line1number.value  + '\n'
            text = text + line2.value  + '\n'
            text = text + line2number.value  + '\n'
            text = text + total.value  + '\n'
            Android.PrintInvoice(text);
            text = ""
    })
var image = "iVBORw0KGgoAAAANSUhEUgAAAK8AAACvAQMAAACxXBw2AAAAB3RJTUUH2woPDAQmGYX3JgAAAAlwSFlzAAAK8AAACvABQqw0mAAAAAZQTFRFAAAA////pdmf3QAAAShJREFUeNrtl0EOhCAMRcvKJTeCmw16M7xRl6xgfkGNmllOITPRpEGfCwv/tyCVT1emB/8TToTLEdloC8t90Me+RiaMHGqoY0cmeXZ2BXqRGY1LxJqsnTGE6IarDJi8KH9Xpy+uHkRyEndr6uCttjyTaH8tQCWcTBKzYfp4dZq8Ihbtp1JWPDg7Q/8yDGM9kiRHCDhwT1ATxylPWb6PWPhQXhPXWmMDARxdZeiN4yTJwfZkMOZdBk0smTS9nc10WRMtDJuJ2WB8U2a78ECcWo810mTsvK+JJm6NRfraVQY9XJupr1OXnSSc+7cWboeFuGVyNNMBWLZRuG/bR177BqOPAWF6w92wr+Xt4e95IBYZcFRAclL7XXA7LADDg4ED6+Ov/CA8+NfwG147q3gE6HAPAAAAAElFTkSuQmCC"

var buttonImg = document.getElementById("img")

buttonImg.addEventListener('click', function(){
Android.print_image(image);
})
};