
#include "ofApp.h"

//--------------------------------------------------------------
void ofApp::setup() {

	
	ofSetVerticalSync(true);
	ofSetWindowTitle("Final Project");
	ofSetFrameRate(60);

	addSphere.addListener(this, &ofApp::addSpherePressed);
	addCone.addListener(this, &ofApp::addConePressed);
	addBox.addListener(this, &ofApp::addBoxPressed);
	
	easycamGroup.setName("easyCam");
	easycamGroup.add(selectEasy.set("Activate camera", false));
	
	camerasGroup.add(easycamGroup);

	cvGroup.setName("openCV");
	cvGroup.add(selectcv.set("Activate OpenCV", false));
	cvGroup.add(activatel.set("Activate Lighting", false));
	camerasGroup.add(cvGroup);
	cameras.setup(camerasGroup);
	cameras.setName("Extras");
	cameras.setPosition(10,500);



	gui.setup();
	gui.setName("Create");
	gui.setPosition(10, 250);
	gui.add(clearBtn.setup("clear"));
	gui.add(addSphere.setup("Add Sphere"));
	gui.add(addCone.setup("Add Cone"));
	gui.add(addBox.setup("Add box"));
	gui.add(radius.setup("radius", 45, 10, 300));
	gui.add(height.setup("height", 45, 10, 300));
	gui.add(width.setup("width", 45, 10, 300));
	gui.add(depth.setup("depth", 45, 10, 300));

	
	boxesGroup.setName("Boxes");
	boxesGroup.add(selectBox.set("Boxes", false));
	boxesGroup.add(sizeObjectB.set("radius", 45, 10, 300));
	boxesGroup.add(posXB.set("X", 300, 0, ofGetWidth()));
	boxesGroup.add(posYB.set("Y", ofGetHeight() / 2, 0, ofGetHeight()));
	boxesGroup.add(posZB.set("Z", 0, 0, 100));
	boxesGroup.add(RB.set("R", 255, 0, 255));
	boxesGroup.add(GB.set("G", 255, 0, 255));
	boxesGroup.add(BB.set("B", 255, 0, 255));
	boxesGroup.add(rotB.set("Rotation", false));
	boxesGroup.add(frameB.set("Frame", false));
	transformsGroup.add(boxesGroup);

	spheresGroup.setName("Spheres");
	spheresGroup.add(selectSphere.set("Spheres", false));
	spheresGroup.add(sizeObjectS.set("radius", 45, 10, 300));
	spheresGroup.add(posXS.set("X", 552, 0, ofGetWidth()));
	spheresGroup.add(posYS.set("Y", ofGetHeight() / 2, 0, ofGetHeight()));
	spheresGroup.add(posZS.set("Z", 0, 0, 100));
	spheresGroup.add(RS.set("R", 255, 0, 255));
	spheresGroup.add(GS.set("G", 255, 0, 255));
	spheresGroup.add(BS.set("B", 255, 0, 255));
	spheresGroup.add(rotS.set("Rotation", false));
	spheresGroup.add(frameS.set("Frame", false));
	transformsGroup.add(spheresGroup);

	conesGroup.setName("Cones");
	conesGroup.add(selectCone.set("Cones", false));
	conesGroup.add(sizeObjectC.set("radius", 45, 10, 300));
	conesGroup.add(posXC.set("X", 740, 0, ofGetWidth()));
	conesGroup.add(posYC.set("Y", ofGetHeight() / 2, 0, ofGetHeight()));
	conesGroup.add(posZC.set("Z", 0, 0, 100));
	conesGroup.add(RC.set("R", 255, 0, 255));
	conesGroup.add(GC.set("G", 255, 0, 255));
	conesGroup.add(BC.set("B", 255, 0, 255));
	conesGroup.add(rotC.set("Rotation",false));
	conesGroup.add(frameC.set("Frame", false));
	transformsGroup.add(conesGroup);

	guiTransforms.setup(transformsGroup);
	guiTransforms.setName("Transforms");
	guiTransforms.setPosition(800, 0);

	
	
	currentPos = 500;
	
	ofSetSmoothLighting(true);
    pointLight.setDiffuseColor( ofFloatColor(.85, .85, .55) );
    pointLight.setSpecularColor( ofFloatColor(1.f, 1.f, 1.f));

    pointLight2.setDiffuseColor( ofFloatColor( 238.f/255.f, 57.f/255.f, 135.f/255.f ));
    pointLight2.setSpecularColor(ofFloatColor(.8f, .8f, .9f));

    pointLight3.setDiffuseColor( ofFloatColor(19.f/255.f,94.f/255.f,77.f/255.f) );
    pointLight3.setSpecularColor( ofFloatColor(18.f/255.f,150.f/255.f,135.f/255.f) );
	
	video.setup(320, 200);
	color.allocate(video.getWidth(), video.getHeight());
	gray.allocate(video.getWidth(), video.getHeight());
	haar.setup("haarcascade_frontalface_default.xml");
	haar.setScaleHaar(3);
}

//--------------------------------------------------------------
void ofApp::update() {

	if (selectcv) {
		video.update();
		if (video.isFrameNew()) {
			color.setFromPixels(video.getPixels());
			gray = color;
			haar.findHaarObjects(gray);

		}
	}

	pointLight.setPosition((ofGetWidth()*.5)+ cos(ofGetElapsedTimef()*.5)*(ofGetWidth()*.3), ofGetHeight()/2, 500);
    pointLight2.setPosition((ofGetWidth()*.5)+ cos(ofGetElapsedTimef()*.15)*(ofGetWidth()*.3),
                            ofGetHeight()*.5 + sin(ofGetElapsedTimef()*.7)*(ofGetHeight()), -300);

    pointLight3.setPosition(
                            cos(ofGetElapsedTimef()*1.5) * ofGetWidth()*.5,
                            sin(ofGetElapsedTimef()*1.5f) * ofGetWidth()*.5,
                            cos(ofGetElapsedTimef()*.2) * ofGetWidth()
    
	);
	
}

//--------------------------------------------------------------
void ofApp::draw() {
	float spinX = sin(ofGetElapsedTimef()*.35f);
	float spinY = cos(ofGetElapsedTimef()*.075f);
	
	if (selectcv) {
		color.draw(0, 0);

		for (int i = 0; i < haar.blobs.size(); i++) {
			ofSetColor(255);
			ofNoFill();
			ofDrawRectangle(haar.blobs[i].boundingRect);
		}
	}

	ofEnableDepthTest();
	

	
	if (activatel) {
		ofEnableLighting();
		pointLight.enable();
		pointLight2.enable();
		pointLight3.enable();
	}

	if (clearBtn) {
		spheres.clear();
		boxes.clear();
		cones.clear();
		currentPos = 500;
	}


	if (selectEasy) {
		cam.begin();
	}
	else {
		cam.end();
	}
	for (auto & s : spheres) {
		if (s.inside(ofGetMouseX(), ofGetMouseY()) && mouseIsPressed == true) {
			s.setPosition(ofGetMouseX(), ofGetMouseY(), zPosition);	
		}
	
		if (selectSphere) {

			s.setPosition(posXS, posYS, posZS);
			s.setScale(sizeObjectS/100);

		}

		

		if (rotateSpheres || rotS) {
			s.rotateDeg(spinX, 1.0, 0.0, 0.0);
			s.rotateDeg(spinY, 0, 1.0, 0.0);
		}

		if (drawSphereWireframe || frameS) {

			ofSetColor(RS, GS, BS);
			s.drawWireframe();
			continue;
		}
		s.draw();
	}

	for (auto & c : cones) {
		if (c.inside(ofGetMouseX(), ofGetMouseY()) && mouseIsPressed == true) {
			c.setPosition(ofGetMouseX(), ofGetMouseY(), zPosition);
		}

		if (selectCone) {
			c.setPosition(posXC, posYC, posZC);
			c.setScale(sizeObjectC / 100);
		}

		if (rotateCones || rotC) {
			c.rotateDeg(spinX, 1.0, 0.0, 0.0);
			c.rotateDeg(spinY, 0, 1.0, 0.0);
		
		}

		if (drawConeWireframe || frameC) {
			
			ofSetColor(RC, GC, BC);
			c.drawWireframe();
			continue;
		}
		c.draw();
	}

	for (auto & b : boxes) {
		if (b.inside(ofGetMouseX(), ofGetMouseY()) && mouseIsPressed == true) {
			b.setPosition(ofGetMouseX(), ofGetMouseY(), zPosition);
		}

		if (selectBox&&~selectcv) {
			b.setPosition(posXB, posYB, posZB);
			b.setScale(sizeObjectB / 100);
		}

		if (rotateBoxes || rotB) {
			b.rotateDeg(spinX, 1.0, 0.0, 0.0);
			b.rotateDeg(spinY, 0, 1.0, 0.0);
		}

		if (selectBox&&selectcv) {
			b.setScale(haar.findHaarObjects(gray));
		}

		if (drawBoxWireframe|| frameB) {
			
			ofSetColor(RB, GB, BB);
			b.drawWireframe();

			continue;
		}
		b.draw();
	}

	ofDisableDepthTest();
	ofDisableLighting();
	cam.end();

	gui.draw();
	guiTransforms.draw();
	cameras.draw();
	
}

//--------------------------------------------------------------
void ofApp::keyPressed(int key) {
	switch (key) {
	case 'b':
		drawBoxWireframe = !drawBoxWireframe;
		break;
	case 'c':
		drawConeWireframe = !drawConeWireframe;
		break;
	case 's':
		drawSphereWireframe = !drawSphereWireframe;
		break;
	case '1':
		rotateBoxes = !rotateBoxes;
		break;
	case '2':
		rotateCones = !rotateCones;
		break;
	case '3':
		rotateSpheres = !rotateSpheres;
		break;
	default:
		break;
	}

}

//--------------------------------------------------------------
void ofApp::keyReleased(int key) {
	
}

//--------------------------------------------------------------
void ofApp::mouseMoved(int x, int y) {

}

//--------------------------------------------------------------
void ofApp::mouseDragged(int x, int y, int button) {
}

//--------------------------------------------------------------
void ofApp::mousePressed(int x, int y, int button) {
	mouseIsPressed = true;
}

//--------------------------------------------------------------
void ofApp::mouseReleased(int x, int y, int button) {
	mouseIsPressed = false;
}

//--------------------------------------------------------------
void ofApp::mouseEntered(int x, int y) {

}

//--------------------------------------------------------------
void ofApp::mouseExited(int x, int y) {

}

//--------------------------------------------------------------
void ofApp::windowResized(int w, int h) {

}

//--------------------------------------------------------------
void ofApp::gotMessage(ofMessage msg) {

}

//--------------------------------------------------------------
void ofApp::dragEvent(ofDragInfo dragInfo) {

}

void ofApp::exit() {
	addSphere.removeListener(this, &ofApp::addSpherePressed);
	addCone.removeListener(this, &ofApp::addConePressed);
	addBox.removeListener(this, &ofApp::addBoxPressed);
}

void ofApp::addSpherePressed() {
	spheres.emplace_back(radius);

}

void ofApp::addBoxPressed() {
	boxes.emplace_back(height, width, depth);
}

void ofApp::onMouseIn(ofVec2f & e)
{
}

void ofApp::addConePressed(){
	cones.emplace_back(radius, height);
}