using System;
using System.Collections;
using System.Collections.Generic;
using TMPro;
using UnityEngine.UI;
using UnityEngine;
using UnityEngine.Serialization;

public class PlayerLivesScore : MonoBehaviour
{
    private int _score;
    public static PlayerLivesScore ActualScore;
    public int lives = 3;
    public TextMeshProUGUI livesText;
    public TextMeshProUGUI scoreText;
    
    private void Start()
    {
        ActualScore = this;
    }

    private void Update()
    {
        livesText.text = "X " + lives;
        scoreText.text = "Score: " + _score;
    }

    private void Damage()
    {
        lives--;
        if (lives >= 0) return;
        Destroy(gameObject);
        GameOver.Over.Lose();
    }
    
    public void IncreaseScore()
    {
        _score++;
    }
}

