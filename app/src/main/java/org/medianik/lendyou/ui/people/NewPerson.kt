package org.medianik.lendyou.ui.people

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.medianik.lendyou.R
import org.medianik.lendyou.model.Repos
import org.medianik.lendyou.model.SnackbarManager
import org.medianik.lendyou.ui.component.LendyouSurface
import org.medianik.lendyou.ui.theme.LendyouTheme

private val emailRegex = Regex("^(.+)@(\\S+)$")

@Composable
fun NewPerson(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    LendyouSurface {
        Column(
            modifier
                .padding(12.dp)
                .padding(top = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val input = remember { mutableStateOf("") }
            var isError = remember { mutableStateOf(false) }
            EmailField(
                input,
                isError,
                Modifier.fillMaxWidth()
            ) { Text(stringResource(R.string.email)) }

            Button(
                onClick = {
                    if (emailRegex.matches(input.value)) {
                        val added = Repos.getInstance().addPerson(input.value)
                        if (added) {
                            SnackbarManager.showMessage(R.string.add_new_person_sent)
                            navigateBack()
                        } else {
                            isError.value = true
                        }
                    } else {
                        isError.value = true
                    }
                },
                modifier = Modifier.padding(12.dp),
                colors = ButtonDefaults.buttonColors(
                    LendyouTheme.colors.brandSecondary,
                    LendyouTheme.colors.textSecondary,
                )
            ) {
                Text(text = "Add")
            }
        }
    }
}

@Composable
fun EmailField(
    input: MutableState<String>,
    isError: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    label: @Composable () -> Unit,
) {
    OutlinedTextField(
        value = input.value,
        onValueChange = { newValue ->
            input.value = newValue
            isError.value = false
        },
        label = label,
        singleLine = true,
        modifier = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        isError = isError.value,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            errorBorderColor = LendyouTheme.colors.error
        ),
    )
}
