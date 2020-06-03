#pragma once

#include <stdlib.h>
#include <stdio.h>
#include <iostream> 
#include <math.h> 
#include "ofxGui.h"
#include "CustomSphere.h"
#include "CustomBox.h"
#include "CustomCone.h"
#include "ofxCvHaarFinder.h"

using namespace std;

#include "ofMain.h"

class ofApp : public ofBaseApp {

public:

	void setup();
	void update();
	void draw();

	void keyPressed(int key);
	void keyReleased(int key);
	void mouseMoved(int x, int y);
	void mouseDragged(int x, int y, int button);
	void mousePressed(int x, int y, int button);
	void mouseReleased(int x, int y, int button);
	void mouseEntered(int x, int y);
	void mouseExited(int x, int y);
	void windowResized(int w, int h);
	void dragEvent(ofDragInfo dragInfo);
	void gotMessage(ofMessage msg);
	
	void addSpherePressed();
	void addConePressed();
	void addBoxPressed();
	void onMouseIn(ofVec2f & e);
	void exit();

	ofxPanel gui;

	ofxButton addSphere;
	ofxButton addBox;
	ofxButton addCone;
	ofxButton clearBtn;

	ofxFloatSlider radius;
	ofxFloatSlider height;
	ofxFloatSlider width;
	ofxFloatSlider depth;

	ofxPanel cameras;
	ofParameterGroup camerasGroup;
	ofParameterGroup easycamGroup;
	ofParameter<bool> selectEasy;
	ofParameter<float> distance;
	ofParameterGroup cvGroup;
	ofParameter<bool> selectcv;
	ofParameter<bool> activatel;


	ofxPanel guiTransforms;
	ofParameterGroup transformsGroup;
	ofParameterGroup spheresGroup;
	ofParameter<bool> selectSphere;
	ofParameter<float> sizeObjectS;
	ofParameter<float> posXS;
	ofParameter<float> posYS;
	ofParameter<float> posZS;
	ofParameter<float> RS;
	ofParameter<float> GS;
	ofParameter<float> BS;
	ofParameter<bool> rotS;
	ofParameter<bool> frameS;


	ofParameterGroup conesGroup;
	ofParameter<bool> selectCone;
	ofParameter<float> sizeObjectC;
	ofParameter<float> posXC;
	ofParameter<float> posYC;
	ofParameter<float> posZC;
	ofParameter<float> RC;
	ofParameter<float> GC;
	ofParameter<float> BC;
	ofParameter<bool> rotC;
	ofParameter<bool> frameC;


	ofParameterGroup boxesGroup;
	ofParameter<bool> selectBox;
	ofParameter<float> sizeObjectB;
	ofParameter<float> posXB;
	ofParameter<float> posYB;
	ofParameter<float> posZB;
	ofParameter<float> RB;
	ofParameter<float> GB;
	ofParameter<float> BB;
	ofParameter<bool> rotB;
	ofParameter<bool> frameB;


	std::list<CustomSphere> spheres;
	std::list<CustomBox> boxes;
	std::list<CustomCone> cones;

	bool mouseIsPressed = false;
	bool drawBoxWireframe = false;
	bool drawConeWireframe = false;
	bool drawSphereWireframe = false;
	bool bDrawLights = false;
	bool rotateCones = false;
	bool rotateBoxes = false;
	bool rotateSpheres = false;

	int zPosition;
	int currentPos;

	ofEasyCam cam;

	ofLight pointLight;
    ofLight pointLight2;
    ofLight pointLight3;

	ofVideoGrabber video;
	ofxCvColorImage color;
	ofxCvGrayscaleImage gray;
	ofxCvHaarFinder haar;
};