// Dialog field cross validation.
function checkFields(dialog) {
    var textfieldArray = dialog.findByType("textfield");
    var resultCheck = true;
        for (var i = 0; i < textfieldArray.length; i++) {
            if (!validateURL(textfieldArray[i])){
                resultCheck = false;
                break;
            }
        }
        return resultCheck;
    };

    function validateURL(linkAddress) {
        var pattern = new RegExp('^(http|https|ftp):\/\/(www\.)?');
        if (linkAddress.getValue().trim() == "#") {
            return true;
        } else if (!pattern.test(linkAddress.getValue().trim())) {
            linkAddress.markInvalid();
            return false;
        } else {
            return true;
        }
    }