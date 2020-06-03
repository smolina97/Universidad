using System.Collections;
using System.Collections.Generic;
using UnityEngine.SceneManagement;
using UnityEngine;
using UnityEngine.Serialization;

public class GameOver : MonoBehaviour
{
     public GameObject gameOverMenu;
     public GameObject victoryMenu;
     public GameObject asteroids;
     public GameObject asteroidsSmallL;
     public GameObject asteroidsSmallR;
     public static GameOver Over;
 
    private void Start()
    {
        
        Over = this;
        gameOverMenu.SetActive(false);
        victoryMenu.SetActive(false);
        
    }

    public void Restart()
    {
        SceneManager.LoadScene("Game");
    }
    public void Lose()
    {
       gameOverMenu.SetActive(true); 
    }
    
    public void Victory()
    { 
        victoryMenu.SetActive(true); 
        asteroids.SetActive(false);
        asteroidsSmallL.SetActive(false);
        asteroidsSmallR.SetActive(false);
    }
}
