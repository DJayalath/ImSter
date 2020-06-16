# ImSter - <ins>Im</ins>age <ins>St</ins>eganograph<ins>er</ins>

ImSter is a tool that lets you hide and view text inside images securely. 

![Main view of GUI](images/mainView.png)

Text is password encrypted using AES and encoded into the pixels of the image themselves
rather than any metadata. It is impossible for anyone to even know that there is hidden content within
an image.

## Demo
The image on the right contains the entirety of Shakespeare's Macbeth encrypted and hidden in its pixels.

Can you tell the difference?

Original Image            |  Image with Macbeth
:-------------------------:|:-------------------------:
![Original image](images/original.png)  |  ![Image with Macbeth hidden inside](images/hidden.png)

To see for yourself, download the image on the right and decode it with ImSter using the password `ronyon`.

## Purpose
While many solutions exist for securely encrypting data, ImSter serves a slightly different purpose. The purpose
 of ImSter is to keep the fact that your information even exists, confidential in addition to providing security through encryption.
 
 ## Quick Start

1. Download the latest [release](https://github.com/armytricks/ImSter/releases/latest)
2. Make sure you have Java installed (at least JRE)
3. Ensure the jar file is executable (you may need to `chmod +x ImSter-xxx.jar` on Linux/Mac OS)
4. Run the jar file (if opening it fails, try `java -jar ImSter-xxx.jar`)

## [License](LICENSE)

<!---
Add libraries, manual build?, and license, and how it works?
-->