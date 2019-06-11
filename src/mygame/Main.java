package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import static org.lwjgl.opengl.Display.getHeight;
import static org.lwjgl.opengl.Display.getWidth;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author Felipe Galhardo 160121
 *         Rodolfo Augusto 163627
 */
public class Main extends SimpleApplication implements PhysicsCollisionListener, ActionListener {

  static Main app;
    public static void main(String[] args) {
        AppSettings settings = new AppSettings(true);
        settings.setResolution(1500, 900);        
        app = new Main();
        app.showSettings = false;
        app.setSettings(settings);
        app.start();
    }
private BulletAppState bulletAppState;
    private PlayerCamera player;
    private boolean direita = false, esquerda = false;
    private Node enemies = new Node("Inimigos");
    private int vida = 1000;
    private int count = 0;
    private int countVida = 0;
    private BitmapText text;
    private BitmapText fimDoJogo;
    private boolean pause;
    private AudioNode batida, life;
  
    
    @Override
    public void simpleInitApp() {
        
        criaFisica();
        criaAgua();
        criaGrama();
        criaChao();
        criaParedes();
        criaLuz();
        criaPlayer();
        criaTeclado();        
        criaPlacar();
        criaSom();
        bulletAppState.setDebugEnabled(false);
        bulletAppState.getPhysicsSpace().addCollisionListener(this);
    }
    
    private void criaInimigos() {
        float posicaox;

        if(player.getLocalTranslation().z < 7900){
            if(countVida != 10){
                posicaox = (float) (Math.random() * 10 - 5);
                Spatial ini = assetManager.loadModel("Models/Ferrari/Car.mesh.xml");
                ini.setName("inimigos");
                ini.scale(0.50f);
                RigidBodyControl corpoRigido = new RigidBodyControl(0.1f);
                ini.addControl(corpoRigido);
                ini.setLocalTranslation(posicaox, 1.5f, player.getLocalTranslation().z + 100f);
                corpoRigido.setPhysicsLocation(ini.getLocalTranslation());
                bulletAppState.getPhysicsSpace().add(ini);
                enemies.attachChild(ini);
                countVida++;
            }
            else{
                posicaox = (float) (Math.random() * 10 - 5);
                Box mesh = new Box(0.7f, 0.7f, 0.7f);
                Geometry geo = new Geometry("vida", mesh);
                Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                mat.setTexture("ColorMap", assetManager.loadTexture("Textures/Eng1.jpg"));
                geo.setMaterial(mat);
                RigidBodyControl corpoRigido = new RigidBodyControl(0.1f);
                geo.addControl(corpoRigido);
                geo.setLocalTranslation(posicaox, 1.5f, player.getLocalTranslation().z + 100f);
                corpoRigido.setPhysicsLocation(geo.getLocalTranslation());
                bulletAppState.getPhysicsSpace().add(geo);
                enemies.attachChild(geo);
                countVida = 0;
            }
        }        

        rootNode.attachChild(enemies);
    }


    private void criaFisica(){
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().addCollisionListener(this);
    }
    
    private void criaTeclado() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Restart", new KeyTrigger(KeyInput.KEY_R));
        inputManager.addListener(this, "Left", "Right", "Restart");
    }
    
    
     private void criaPlayer() {
        player = new PlayerCamera("player", assetManager, bulletAppState, cam);
        rootNode.attachChild(player);
        flyCam.setEnabled(true);
    }
    
    private void criaChao(){
        Box boxMesh = new Box(7f,1f,4500f); 
        Geometry boxGeo = new Geometry("Colored Box", boxMesh); 
        Material boxMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture monkeyTex = assetManager.loadTexture("Textures/pista.png");
        boxMat.setTexture("ColorMap", monkeyTex);
        boxGeo.setMaterial(boxMat);
        boxGeo.setLocalTranslation(0f, 0f, 3990f);
        rootNode.attachChild(boxGeo);
        
        
        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape(boxGeo);
        RigidBodyControl RigidBody = new RigidBodyControl(sceneShape, 0);
        boxGeo.addControl(RigidBody);

        bulletAppState.getPhysicsSpace().add(RigidBody);         
    }
    private void criaGrama(){
        Box boxMesh = new Box(20f,0f,3000f); 
        Geometry boxGeo = new Geometry("Colored Box", boxMesh); 
        Material boxMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture monkeyTex = assetManager.loadTexture("Textures/grama.jpg");
        boxMat.setTexture("ColorMap", monkeyTex);
        boxGeo.setMaterial(boxMat);
        boxGeo.setLocalTranslation(0f, -0.5f, 3990f);
        rootNode.attachChild(boxGeo);
              
    }
    
     private void criaAgua(){
        Box boxMesh = new Box(20f,0f,4500f); 
        Geometry boxGeo = new Geometry("Colored Box", boxMesh); 
        Material boxMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture monkeyTex = assetManager.loadTexture("Textures/agua.jpg");
        boxMat.setTexture("ColorMap", monkeyTex);
        boxGeo.setMaterial(boxMat);
        boxGeo.setLocalTranslation(0f, -1f, 3990f);
        rootNode.attachChild(boxGeo);
    }

    
    private void criaParedes(){
        Box boxMesh = new Box(1f, 0.001f , 20f);
        Geometry boxGeo = new Geometry("Colored Box", boxMesh); 
        Material boxMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md"); 
        boxMat.setBoolean("UseMaterialColors", true); 
        boxMat.setColor("Ambient", ColorRGBA.White); 
        boxMat.setColor("Diffuse", ColorRGBA.White); 
        boxGeo.setMaterial(boxMat);
        boxGeo.setLocalTranslation(7f, 2.5f, 3990f);

        rootNode.attachChild(boxGeo);
        
        RigidBodyControl RigidBody = new RigidBodyControl(0);
        boxGeo.addControl(RigidBody);
        
        RigidBody.setPhysicsLocation(boxGeo.getLocalTranslation());

        bulletAppState.getPhysicsSpace().add(RigidBody);
        
        Box boxMesh2 = new Box(1f, 0.001f , 20f);
        Geometry boxGeo2 = new Geometry("Colored Box", boxMesh2); 
        Material boxMat2 = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md"); 
        boxMat2.setBoolean("UseMaterialColors", true); 
        boxMat2.setColor("Ambient", ColorRGBA.White); 
        boxMat2.setColor("Diffuse", ColorRGBA.White); 
        boxGeo2.setMaterial(boxMat2); 
        boxGeo2.setLocalTranslation(-7f, 2.5f, 3990f);
        rootNode.attachChild(boxGeo2);
        
        RigidBodyControl RigidBody2 = new RigidBodyControl(0);        
        boxGeo2.addControl(RigidBody2);
        
        RigidBody2.setPhysicsLocation(boxGeo2.getLocalTranslation());

        bulletAppState.getPhysicsSpace().add(RigidBody2);
        
    }
    
    private void criaLuz() {

        DirectionalLight sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-10.5f, -15f, -10.5f)).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);

        DirectionalLight sun2 = new DirectionalLight();
        sun2.setDirection((new Vector3f(10.5f, -15f, 10.5f)).normalizeLocal());
        sun2.setColor(ColorRGBA.White);
        rootNode.addLight(sun2);
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        
            
            
        if(!pause){
            
            player.upDateKeys(tpf, direita, esquerda, player);
            count++;
        
        if(count > 150){
            criaInimigos();
            count = 0;
        }
        
        text.setText("Vida = " + String.valueOf(vida));
        text.setSize(15f);
         text.setLocalTranslation(getWidth()/2, getHeight()/2-10, 0);
                
        for(Spatial enemy : enemies.getChildren())
       
            enemy.rotate(0, tpf, 0);
        
        if(player.getLocalTranslation().z >= 7985f){
            text.setText(" ");
            fimDoJogo.setColor(ColorRGBA.Yellow);
            fimDoJogo.setText("Parabens, voce completou o jogo!!");
            fimDoJogo.setLocalTranslation(10+getWidth()/2, getHeight()/2, 0);
            fimDoJogo.setSize(25);
            guiNode.attachChild(fimDoJogo);
            pause = true;
            bulletAppState.setEnabled(false);   
        }
  
        if(vida <= 0){
            text.setText(" ");
            fimDoJogo.setColor(ColorRGBA.Red);
            fimDoJogo.setText("Você perdeu! Aperte R para continuar!!");
            fimDoJogo.setLocalTranslation(10+getWidth()/2, getHeight()/2, 0);
            fimDoJogo.setSize(25);
            guiNode.attachChild(fimDoJogo);
            pause = true;
            bulletAppState.setEnabled(false);           
        }
        }
    }
    
    protected void criaPlacar() {
        guiNode.detachAllChildren();
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        text = new BitmapText(guiFont, false);
        text.setSize(guiFont.getCharSet().getRenderedSize());        
        text.setColor(ColorRGBA.Green);
        text.setSize(30);
        text.setLocalTranslation(0, settings.getHeight() - 70, 0);
        guiNode.attachChild(text);

        fimDoJogo = new BitmapText(guiFont, false);
        fimDoJogo.setSize(guiFont.getCharSet().getRenderedSize());

        fimDoJogo.setLocalTranslation((settings.getWidth() / 2) - (guiFont.getCharSet().getRenderedSize() * (fimDoJogo.getText().length() / 2 + 13)),
                settings.getHeight() + fimDoJogo.getLineHeight() / 2 - 100, 0);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    @Override
    public void collision(PhysicsCollisionEvent event) {
        Spatial nodeA = event.getNodeA();
        Spatial nodeB = event.getNodeB();
              
        if(nodeA.getName().equals("player"))
        {
            if(nodeB.getName().equals("inimigos")){
             vida -= 100;
             rootNode.detachChild(nodeB);
             enemies.detachChild(nodeB);
             bulletAppState.getPhysicsSpace().remove(nodeB);
             batida(nodeB.getLocalTranslation());
             batida.playInstance();
            }
        }
        else if(nodeB.getName().equals("player")){
            if(nodeA.getName().equals("inimigos")){
                vida -= 100;
                rootNode.detachChild(nodeA);
                enemies.detachChild(nodeA);
                bulletAppState.getPhysicsSpace().remove(nodeA);
                batida(nodeA.getLocalTranslation());
                batida.playInstance();
            }
        }
        if(nodeA.getName().equals("player"))
        {
            if(nodeB.getName().equals("vida")){
             vida += 100;
             rootNode.detachChild(nodeB);
             enemies.detachChild(nodeB);
             bulletAppState.getPhysicsSpace().remove(nodeB);
             batida(nodeB.getLocalTranslation());
             life.playInstance();
            }
        }
        else if(nodeB.getName().equals("player")){
            if(nodeA.getName().equals("vida")){
                vida += 100;
                rootNode.detachChild(nodeA);
                enemies.detachChild(nodeA);
                bulletAppState.getPhysicsSpace().remove(nodeA);
                batida(nodeA.getLocalTranslation());
                life.playInstance();
            }
        }        
    }

    @Override
    public void onAction(String name, boolean value, float tpf) {
        switch (name) {
            case "Left":
                direita = value;
                break;
            case "Right":
                esquerda = value;
                break;
            case "Restart":
                vida=1000;
                pause=false;
                count = 0;                
                bulletAppState.getPhysicsSpace().removeAll(enemies);
                bulletAppState.getPhysicsSpace().removeAll(player);
                rootNode.detachChild(player);
                rootNode.detachChild(enemies);  
                bulletAppState.setEnabled(true);
                guiNode.detachChild(fimDoJogo);
                enemies = new Node();
                
                bulletAppState.setDebugEnabled(false);
                criaPlayer();
                break;                
        }
    }
    
    private void batida(Vector3f pos) {
        pos.y += 2;
        ParticleEmitter debrisEffect = new ParticleEmitter("Debris", ParticleMesh.Type.Triangle, 100);
        debrisEffect.setLocalTranslation(pos);
        Material debrisMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        debrisMat.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/Debris.png"));
        debrisEffect.setMaterial(debrisMat);
        debrisEffect.setImagesX(3);
        debrisEffect.setImagesY(3); // 3x3 texture animation
        debrisEffect.setRotateSpeed(4);
        debrisEffect.setSelectRandomImage(true);
        debrisEffect.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 2, 0));
        debrisEffect.setStartColor(new ColorRGBA(1f, 1f, 1f, 1f));
        debrisEffect.setGravity(0f, 6f, 0f);
        debrisEffect.getParticleInfluencer().setVelocityVariation(.60f);
        debrisEffect.setHighLife(3000);
        rootNode.attachChild(debrisEffect);
        debrisEffect.emitAllParticles();
    }
    
    
    private void criaSom(){
        batida = new AudioNode(assetManager, "Sounds/batida.wav", false);
        batida.setLooping(false);
        batida.setVolume(2);
        rootNode.attachChild(batida);
        
        life = new AudioNode(assetManager, "Sounds/Eng.wav", false);
        life.setLooping(false);
        life.setVolume(5);
        rootNode.attachChild(batida);
         
    }
}