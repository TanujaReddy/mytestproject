/*
 *  Copyright 2014 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
new hobs.TestSuite("strut-amway Tests", {path:"/apps/corporate/amway-today/tests/SampleTests.js", register: true})

    .addTestCase(new hobs.TestCase("Navigate to root page")
        .navigateTo("/content/asia-pac/australia-new-zealand/australia/amway-today.html")
        .asserts.location("/content/asia-pac/australia-new-zealand/australia/amway-today.html", true)
        .asserts.isTrue(function() {
			return hobs.find("p").text().contains("This page redirects to English");
        })
    )

    .addTestCase(new hobs.TestCase("Navigate to english page")
        .navigateTo("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au.html")
        .asserts.location("/content/asia-pac/australia-new-zealand/australia/amway-today/en_au.html", true)
        .asserts.visible(".servicecomponent", true)
    )

    .addTestCase(new hobs.TestCase("Navigate to french page")
        .navigateTo("/content/asia-pac/australia-new-zealand/australia/amway-today/fr.html")
        .asserts.location("/content/asia-pac/australia-new-zealand/australia/amway-today/fr.html", true)
        .asserts.visible(".servicecomponent", true)
    );
