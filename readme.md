# Image to 3d structure converter

This application reads an image file and converts it to STL format.

The STL format can be used to 3d print the image and then used as a lamp shade.
The structure will be thicker where the image is darker and thinner where the image is lighter.
That way the image will be visible only when the lamp is turned on.

The name of the file and the parameters can be set in the config file.
The name of the config file is `image23d.properties`.
It should be in the same folder as the executable.

## Properties configurable



* `image` must present in the configuration file, and it has to specify the input file name.
The output file name is automatically calculated from it shopping off the extension and replacing it with `.stl`.
This is the only mandatory parameter.
* `scale` is the scaling of the image.
The default value is `1.0` which means that the image is not scaled.
Scale changes the size of the image in pixels. If scale is 0.5 then the number of the pixels are halved in both linear directions.
* `pixel` is the size of one pixel in millimeters.
The default value is `0.1`.
* `layer` is the thickness of the layer in millimeters.
The default value is `0.02`.
The scale of the image is 0 to 255 in grayscale.
This is multiplied by the value of the parameter to get the thickness of the output at a certain point.
Since the thickness is proportional with the amount of light consumed, therefore the darker the pixel the thicker the layer.
A grayscale value `x` will be used as `(255-x)*layer` in millimeters.
* `base` is the base of the image in millimeters.
When calculating the height of each point this value is added to the result.
This will result a baseplate under the image.
The default value is `1.0`, meaning one millimeter.
* `flip` is a boolean value, either `true` or `false`.
The default value is `false`, which means that the picture will not be flipped.
If the value is `true` then the picture will be flipped along the vertical axe.
