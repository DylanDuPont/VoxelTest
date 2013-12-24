package mineplex;

import java.util.ArrayList;
import java.util.Random;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.light.SpotLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;

public class Main extends SimpleApplication {
	public SpotLight spot;
	public DirectionalLight sun;
	
	public static Main app;
	public boolean sunOn = true;
	
	public Dimension overworld;
	
	public ArrayList<String> keys;
	public TileRegistry tileRegistry;
	
	public Random r;
	
    public static void main(String[] args) {
    	AppSettings settings = new AppSettings(true);
    	settings.setResolution(1280,720);
    	
        app = new Main();
        app.setShowSettings(false);
        app.setSettings(settings);
        app.start();
    }

    public static Main getInstance() {
    	return app;
    }
    
    @Override
    public void simpleInitApp() {
    	viewPort.setBackgroundColor(ColorRGBA.Cyan);
    	
        overworld = new Dimension(this);
    	
    	tileRegistry = new TileRegistry();
    	tileRegistry.registerTile(new TileGrass(0, 0, 0, overworld));
    	tileRegistry.registerTile(new TileStone(0, 0, 0, overworld));
    	tileRegistry.registerTile(new TileWater(0, 0, 0, overworld));
    	tileRegistry.registerTile(new TileSand(0, 0, 0, overworld));
    	
        overworld.generateLevel(123213);
        
        /*Spatial batcave = assetManager.loadModel("Models/batcave3DS/batcave.j3o");
        Material mat_default = new Material( 
            assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
        batcave.setMaterial(mat_default);
        batcave.scale(100f, 100f, 100f);
        rootNode.attachChild(batcave);*/
        
        sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f).normalizeLocal());
        rootNode.addLight(sun);
        
        spot = new SpotLight();
        spot.setSpotRange(100f);                           // distance
        spot.setSpotInnerAngle(15f * FastMath.DEG_TO_RAD); // inner light cone (central beam)
        spot.setSpotOuterAngle(35f * FastMath.DEG_TO_RAD); // outer light cone (edge of the light)
        spot.setColor(ColorRGBA.White.mult(1.3f));         // light color
        spot.setPosition(cam.getLocation());               // shine from camera loc
        spot.setDirection(cam.getDirection());             // shine forward from camera loc
        rootNode.addLight(spot);
        
        cam.setLocation(new Vector3f(5.592955E-4f, 0.010915078f, 6.214143f));
        cam.setRotation(new Quaternion(-5.7244706E-5f, 0.9998851f, 0.014648534f, 0.003906424f));
        
        flyCam.setEnabled(true);
        flyCam.setMoveSpeed(7);
        flyCam.setRotationSpeed(5);
        
        keys = new ArrayList<String>();
        
        keys.add("Debug: Toggle Sun");
        
        registerKeybinds();
    }
    
    public void registerKeybinds() {
        for(String s : keys) {
            inputManager.addMapping(s, new KeyTrigger(KeyInput.KEY_P));
        }
        
        ActionListener actionListener = new ActionListener() {
        	public void onAction(String name, boolean keyPressed, float tpf) {
        		if(name.equals("Debug: Toggle Sun") && keyPressed) {
        			if(sunOn)
        				rootNode.removeLight(sun);
        			else
        				rootNode.addLight(sun);
        			
        			sunOn = !sunOn;
        		}
        	}
        };
        
        inputManager.addListener(actionListener, keys.toArray(new String[keys.size()]));
    }

    @Override
    public void simpleUpdate(float tpf) {
        spot.setPosition(cam.getLocation());
        spot.setDirection(cam.getDirection());
        
        overworld.update();
        
        if(sunOn)
        	viewPort.setBackgroundColor(ColorRGBA.Cyan);
        else
        	viewPort.setBackgroundColor(ColorRGBA.Black);
        
        rootNode.updateModelBound();
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
